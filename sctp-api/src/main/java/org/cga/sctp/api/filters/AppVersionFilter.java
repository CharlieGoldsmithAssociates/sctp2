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

package org.cga.sctp.api.filters;

import com.google.gson.Gson;
import org.cga.sctp.api.core.AppConstants;
import org.cga.sctp.api.core.ErrorResponse;
import org.cga.sctp.mobile.AppVersion;
import org.cga.sctp.mobile.MobileApplicationService;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Filter to add application version headers to responses</p>
 */
public class AppVersionFilter extends GenericFilterBean {

    private final Gson gson;
    private final boolean filterEnabled;
    private final AtomicReference<AppVersion> appVersion;
    private final List<AntPathRequestMatcher> ignoredPathMatchers;
    private final MobileApplicationService mobileApplicationService;

    public AppVersionFilter(MobileApplicationService mobileApplicationService, boolean filterEnabled, Gson gson, String... ignoredPaths) {
        this.gson = gson;
        this.appVersion = new AtomicReference<>();
        this.ignoredPathMatchers = new LinkedList<>();
        this.mobileApplicationService = mobileApplicationService;
        if (ignoredPaths.length > 0) {
            for (String pathPattern : ignoredPaths) {
                this.ignoredPathMatchers.add(new AntPathRequestMatcher(pathPattern));
            }
        }
        this.filterEnabled = filterEnabled;
        this.refreshVersion();
    }

    public AppVersionFilter(MobileApplicationService mobileApplicationService, Gson gson, String... ignoredPaths) {
        this(mobileApplicationService, true, gson, ignoredPaths);
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        int applicationVersionCode = -1;
        AppVersion version = appVersion.get();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!filterEnabled) {
            if (version != null) {
                response.addHeader(AppConstants.APP_VERSION_UPDATE_AVAILABLE_HEADER, "false");
                response.addHeader(AppConstants.APP_VERSION_CODE_HEADER, version.getVersionCode().toString());
                response.addHeader(AppConstants.APP_VERSION_UPDATE_TIME_HEADER, version.getUpdatedAt().toString());
                response.addHeader(AppConstants.APP_VERSION_UPDATE_MANDATORY, version.getMandatoryUpdate().toString());
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (ignorePath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            applicationVersionCode = request.getIntHeader(AppConstants.APP_VERSION_CODE_HEADER);
        } catch (Exception e) {
            logger.error("Failed to parse application version code", e);
        }

        final Boolean updateAvailable;
        final boolean validApplicationCode;

        if (applicationVersionCode >= 1) {
            if (version != null) {
                validApplicationCode = applicationVersionCode <= version.getVersionCode();
            } else {
                validApplicationCode = false;
            }
        } else {
            validApplicationCode = false;
        }

        if (!validApplicationCode) {
            String error = "Unsupported application version.";
            sendErrorResponse(response, HttpServletResponse.SC_PRECONDITION_FAILED, error);
            return;
        }

        updateAvailable = applicationVersionCode < version.getVersionCode();

        response.addHeader(AppConstants.APP_VERSION_CODE_HEADER, version.getVersionCode().toString());
        response.addHeader(AppConstants.APP_VERSION_UPDATE_TIME_HEADER, version.getUpdatedAt().toString());
        response.addHeader(AppConstants.APP_VERSION_UPDATE_AVAILABLE_HEADER, updateAvailable.toString());
        response.addHeader(AppConstants.APP_VERSION_UPDATE_MANDATORY, version.getMandatoryUpdate().toString());

        if (updateAvailable && version.getMandatoryUpdate()) {
            String error = "Application is out of date. Please update. This is a mandatory update";
            sendErrorResponse(response, HttpServletResponse.SC_PRECONDITION_FAILED, error);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(code, message);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(code);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(gson.toJson(errorResponse));
        printWriter.flush();
    }

    public void refreshVersion() {
        this.appVersion.set(mobileApplicationService.getLatestVersion());
    }
}
