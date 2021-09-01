/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
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

package org.cga.sctp.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.cga.sctp.core.BaseService;
import org.cga.sctp.persistence.StatusCode;
import org.cga.sctp.security.AccessControlService;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.User;
import org.cga.sctp.user.UserService;
import org.cga.sctp.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class AuthService extends BaseService implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthConfiguration authConfiguration;

    @Value("${auth.jwt.iss}")
    private String jwtIssuer;

    public AuthService() {
        jwtIssuer = "";
    }

    /**
     * Authenticate a principal
     *
     * @param authentication Authentication details containing username and password
     * @return .
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final User user;
        final AuthenticationResult result;
        final String username = (String) authentication.getPrincipal();
        final String password = (String) authentication.getCredentials();
        final UsernamePasswordAuthenticationToken token;
        final String ipAddress;

        if ((authentication instanceof HttpRequestAuthentication)) {
            ipAddress = ((HttpRequestAuthentication) authentication).getIpAddress();
        } else if ((authentication instanceof UsernamePasswordAuthenticationToken)) {
            final Object details = authentication.getDetails();
            if (details instanceof WebAuthenticationDetails) {
                ipAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
            } else {
                ipAddress = "N/A";
            }
        } else {
            return null;
        }

        if ((user = userService.findByUserNameIgnoringStatus(username)) == null) {
            publishEvent(AuthenticationEvent.userNotFound(ipAddress, username));
            throw new BadCredentialsException(AuthenticationResult.InvalidCredentials.message);
        }

        if (user.isDeleted()) {
            publishEvent(AuthenticationEvent.deletedUser(ipAddress, user));
            throw new BadCredentialsException(AuthenticationResult.InvalidCredentials.message);
        }

        if (!user.isActive()) {
            publishEvent(AuthenticationEvent.inactiveUser(ipAddress, user));
            throw new LockedException(AuthenticationResult.InactiveAccount.message);
        }

        user.setIpAddress(ipAddress);
        user.setLastAuthAttemptAt(LocalDateTime.now());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setAuthAttempts(user.getAuthAttempts() + 1);

            if (user.getAuthAttempts() >= authConfiguration.maxAttempts()) {
                user.setStatusText(format("Locked after %d failed auth attempts.", user.getAuthAttempts()));
                user.setStatus(StatusCode.INACTIVE);
            }

            result = AuthenticationResult.InvalidCredentials;
            publishEvent(AuthenticationEvent.invalidCredentials(ipAddress, user));
            token = null;
        } else {
            user.setAuthAttempts(0);
            token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority(user.getRole().name()))
            );
            token.setDetails(new AuthenticatedUser(user));
            result = AuthenticationResult.Authenticated;
            publishEvent(AuthenticationEvent.authenticated(ipAddress, user));
        }

        // update
        userService.saveUser(user);

        if (result != AuthenticationResult.Authenticated) {
            if (!user.isActive()) {
                publishEvent(AuthenticationEvent.accountLocked(ipAddress, user));
                throw new LockedException(AuthenticationResult.InactiveAccount.message);
            } else {
                throw new BadCredentialsException(AuthenticationResult.InvalidCredentials.message);
            }
        }

        return token;
    }

    public boolean verifyPassword(String plain, String hashed) {
        return passwordEncoder.matches(plain, hashed);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication) ||
                HttpRequestAuthentication.class.equals(authentication);
    }

    public String hashPassword(String newPassword) {
        return passwordEncoder.encode(newPassword);
    }

    public String generateRandomString(int count) {
        return CryptoUtils.genRandomString(count);
    }

    public String generatePassword() {
        return passwordEncoder.encode(generateRandomString(15));
    }

    public String createPasswordResetToken(Key key) {
        Date now = new Date();
        Date expiration = Date.from(now.toInstant().plus(5, ChronoUnit.MINUTES));

        return Jwts.builder()
                .claim("nonce", BCrypt.gensalt())
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String createPasswordResetToken(String key) {
        return createPasswordResetToken(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)));
    }

    public boolean isValidToken(String token, String key) {
        return isValidToken(token, Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)));
    }

    public boolean isValidToken(String token, Key key) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            LOG.error("Failure parsing jwt.", e);
        }
        return false;
    }
}
