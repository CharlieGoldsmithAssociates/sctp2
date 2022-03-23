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

import org.cga.sctp.audit.AuditEvent;
import org.cga.sctp.audit.EventType;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.DistrictUserProfilesView;
import org.cga.sctp.user.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;
import java.util.Map;

public class AuthenticationEvent extends AuditEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AuthenticationEvent(Map<String, Object> source) {
        super(EventType.authentication, source);
    }

    public static ApplicationEvent userNotFound(String ipAddress, String username) {
        Map<String, Object> data = newDataMap(ipAddress);
        data.put("username", username);
        data.put("what", "Not found");
        return new AuthenticationEvent(data);
    }

    private static AuthenticationEvent create(String ipAddress, User user, String status) {
        return create(ipAddress, user.getUserName(), status);
    }

    private static AuthenticationEvent create(String ipAddress, String username, String status) {
        Map<String, Object> data = newDataMap(ipAddress);
        data.put("username", username);
        data.put("what", status);
        return new AuthenticationEvent(data);
    }

    public static AuthenticationEvent deletedUser(String ipAddress, User user) {
        return create(ipAddress, user, "Failed because user is deleted");
    }

    public static AuthenticationEvent inactiveUser(String ipAddress, User user) {
        return create(ipAddress, user, "Failed because user is inactive or disabled");
    }

    public static AuthenticationEvent invalidCredentials(String ipAddress, User user) {
        return create(ipAddress, user, "Failed due to invalid credentials entered");
    }

    public static AuthenticationEvent authenticated(String ipAddress, User user) {
        return create(ipAddress, user, "Authenticated as " + user.getRole().label);
    }

    public static AuthenticationEvent accountLocked(String ipAddress, User user) {
        return create(ipAddress,
                user,
                String.format(Locale.US, "Account locked after %d failed authentication attempts", user.getAuthAttempts())
        );
    }

    public static AuthenticationEvent loggedOut(String ipAddress, AuthenticatedUser user) {
        return create(ipAddress, user.username(), "Logged out");
    }

    public static ApplicationEvent inactiveDistrictUserProfile(DistrictUserProfilesView profile, String ipAddress) {
        return create(ipAddress, profile.getUserName(),
                "Attempt to authenticate through app failed. District user profile is deactivated.");
    }
}
