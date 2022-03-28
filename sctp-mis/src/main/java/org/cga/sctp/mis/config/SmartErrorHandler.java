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

package org.cga.sctp.mis.config;

import org.cga.sctp.mis.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class SmartErrorHandler implements ErrorController {

    private static final String ERROR_PATH = "/error";
    private final Logger logger;

    public SmartErrorHandler() {
        logger = LoggerFactory.getLogger(getClass());
    }

    private boolean isAuthenticated() {
        return SpringUtils.isPrincipalAuthenticated();
    }

    private ModelAndView view(String name) {
        return new ModelAndView("error/" + name);
    }

    @GetMapping(ERROR_PATH)
    ModelAndView handleError(Authentication authentication, HttpServletRequest request) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");

        Map<String, Object> model = new LinkedHashMap<>();
        HttpStatus httpStatus = HttpStatus.valueOf((Integer) status);

        if (exception != null) {
            logger.error("Found exception of {}: {}.", exception.getClass(), exception.getMessage());
        }
        if (httpStatus == HttpStatus.OK) {
            logger.error("Exception was converted into an HTTP 500 error because the response code was still 200 OK");
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        model.put("status", httpStatus.value());
        model.put("error", httpStatus.getReasonPhrase());
        return switch (httpStatus) {
            case NOT_FOUND -> view("404").addAllObjects(model);
            case INTERNAL_SERVER_ERROR -> view("500").addAllObjects(model);
            case FORBIDDEN -> view("403").addAllObjects(model);
            default -> (isAuthenticated() ? view("generic") : view("anon-generic")).addAllObjects(model);
        };
    }

    //@Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
