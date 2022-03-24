/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.api.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.cga.sctp.api.auth.AccessTokenClaims;
import org.cga.sctp.api.core.AppConstants;
import org.cga.sctp.api.user.ApiUserDetails;
import org.cga.sctp.user.User;
import org.cga.sctp.user.UserService;
import org.cga.sctp.utils.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This filter checks for a JTW token in every authenticated request.
 */
public class JwtAuthorizationFilterFilter extends GenericFilterBean {

    private final Logger logger;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final List<AntPathRequestMatcher> ignoredPathMatchers;

    public JwtAuthorizationFilterFilter(UserService userService, JwtUtil jwtUtil, String... ignoredPaths) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.ignoredPathMatchers = new LinkedList<>();
        this.logger = LoggerFactory.getLogger(getClass());
        if (ignoredPaths.length > 0) {
            for (String pathPattern : ignoredPaths) {
                this.ignoredPathMatchers.add(new AntPathRequestMatcher(pathPattern));
            }
        }
    }

    private boolean ignorePath(HttpServletRequest request) {
        for (AntPathRequestMatcher requestMatcher : ignoredPathMatchers) {
            if (requestMatcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        String jwt;
        final HttpServletRequest request;
        final Authentication authentication;

        request = ((HttpServletRequest) servletRequest);

        if (AppConstants.AUTHENTICATION_PATH.equalsIgnoreCase(request.getRequestURI())) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (ignorePath(request)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (!LocaleUtils.isStringNullOrEmpty(jwt = request.getHeader(AppConstants.JWT_HEADER_NAME))) {
            if (jwt.startsWith("Bearer")) {
                jwt = jwt.substring(7);
            }

            authentication = getAuthenticationDetailsFromToken(jwt);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authenticated principal: {}", authentication.getPrincipal());
                chain.doFilter(request, servletResponse);
                return;
            }
        }

        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);

        //SecurityContextHolder.clearContext();
        //chain.doFilter(servletRequest, servletResponse);
    }

    private Authentication getAuthenticationDetailsFromToken(String token) {
        DecodedJWT jwt;
        User user;
        AccessTokenClaims claims;

        if ((jwt = jwtUtil.parseJwt(token)) == null) {
            return null;
        }

        claims = jwtUtil.getAccessTokenClaims(jwt);
        if ((user = userService.findByUserNameAndSessionId(claims.getUserName(), jwt.getId())) == null) {
            logger.warn("Invalid username and session pair for {}: Most likely a revoked session.", claims.getUserName());
            return null;
        }

        // In case user status changed
        if (!user.isActive() || user.isDeleted()) {
            return null;
        }

        // manually load permissions
        UsernamePasswordAuthenticationToken upat
                = new UsernamePasswordAuthenticationToken(user.getUserName(), null, new LinkedList<>() {{
            add(new SimpleGrantedAuthority(user.getRole().name()));
        }});

        upat.setDetails(ApiUserDetails.of(user, claims));

        return upat;
    }

    private String getRequestUri(HttpServletRequest request) {
        final String contextPath = request.getContextPath(), requestUri = request.getRequestURI();
        if (LocaleUtils.isStringNullOrEmpty(contextPath) || contextPath.equalsIgnoreCase("/")) {
            return requestUri;
        }
        return requestUri.substring(contextPath.length());
    }
}
