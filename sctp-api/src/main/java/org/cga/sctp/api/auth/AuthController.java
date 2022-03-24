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

package org.cga.sctp.api.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cga.sctp.api.core.AppConstants;
import org.cga.sctp.api.core.BaseController;
import org.cga.sctp.api.core.IncludeGeneralResponses;
import org.cga.sctp.api.security.JwtInfo;
import org.cga.sctp.api.security.JwtUtil;
import org.cga.sctp.auth.AuthService;
import org.cga.sctp.auth.AuthenticationEvent;
import org.cga.sctp.user.DistrictUserProfilesView;
import org.cga.sctp.user.User;
import org.cga.sctp.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping
@Tag(name = "Authentication", description = "Authentication endpoint")
public class AuthController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(AppConstants.AUTHENTICATION_PATH)
    @Operation(description = "Authenticates the user using username and password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed. Invalid username or password.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Authentication failed. Inactive district user profile or user account.", content = @Content)
    })
    @IncludeGeneralResponses
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @RequestHeader(AppConstants.APP_VERSION_CODE_HEADER) Integer appVersionCode,
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletRequest httpRequest
    ) {
        final User user;
        final JwtInfo jwtInfo;
        final DistrictUserProfilesView profile;
        final String ipAddress = httpRequest.getRemoteAddr();

        if ((profile = userService.findDistrictUserProfileByUsername(request.getUserName())) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        user = userService.findById(profile.getUserId());

        user.setIpAddress(ipAddress);
        user.setLastAuthAttemptAt(LocalDateTime.now());

        if (user.isDeleted() || !user.isActive()) {
            publishEvent(AuthenticationEvent.accountLocked(ipAddress, user));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!profile.getActive()) {
            publishEvent(AuthenticationEvent.inactiveDistrictUserProfile(profile, ipAddress));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!authService.verifyPassword(request.getPassword(), user.getPassword())) {
            user.setAuthAttempts(user.getAuthAttempts() + 1);
            user.setActive(user.getAuthAttempts() >= authService.getMaxAuthAttempts());

            if (!user.isActive()) {
                publishEvent(AuthenticationEvent.accountLocked(ipAddress, user));
                user.setStatusText(format("Locked after %,d failed authentication attempts through Android app.",
                        authService.getMaxAuthAttempts()));
            }

            userService.saveUser(user);
            publishEvent(AuthenticationEvent.authenticated(ipAddress, user));
            return ResponseEntity.status(!user.isActive() ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED).build();
        }

        // Generate token
        jwtInfo = jwtUtil.generateJwt(user, profile);

        user.setAuthAttempts(0);
        user.setSessionId(jwtInfo.getJti());

        userService.saveUser(user);

        publishEvent(AuthenticationEvent.authenticatedFromMobileApp(ipAddress, user, appVersionCode));

        return ResponseEntity.ok(new AuthenticationResponse(jwtInfo.getToken()));
    }
}