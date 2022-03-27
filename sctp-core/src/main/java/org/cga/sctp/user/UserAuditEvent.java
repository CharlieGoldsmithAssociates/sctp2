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

package org.cga.sctp.user;

import org.cga.sctp.audit.AuditEvent;
import org.cga.sctp.audit.EventType;
import org.cga.sctp.location.Location;
import org.springframework.context.ApplicationEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserAuditEvent extends AuditEvent {
    public UserAuditEvent(Object source) {
        super(EventType.user, source);
    }

    private static UserAuditEvent create(String message, Object... args) {
        UserAuditEvent event;
        Map<String, String> logData = new LinkedHashMap<>();
        event = new UserAuditEvent(logData);
        logData.put("what", event.format(message, args));
        return event;
    }

    public static UserAuditEvent created(String creator, String newUser, String ipAddress) {
        return create("%s added user %s, from IP %s", creator, newUser, ipAddress);
    }

    public static UserAuditEvent modified(String principal, String username, String ipAddress) {
        return create("%s modified user %s, from IP %s", principal, username, ipAddress);
    }

    public static ApplicationEvent password(String principal, String username, String ipAddress) {
        return create("%s changed %s's password from IP %s", principal, username, ipAddress);
    }

    public static ApplicationEvent removedFromDistrictProfiles(String principal, DistrictUserProfilesView profile, String ipAddress) {
        return create(
                "%s removed %s's (%s) profile from %s district from IP address %.",
                principal,
                profile.getUserName(),
                profile.getFullname(),
                profile.getDistrictName(),
                ipAddress
        );
    }

    public static ApplicationEvent districtProfileAdded(String principal, User user, Location location, String ipAddress) {
        return create(
                "%s created district user profile for %s(%s) for district %s from IP address %s.",
                principal,
                user.getUserName(),
                user.makeFullName(),
                location.getName(),
                ipAddress
        );
    }

    public static ApplicationEvent districtProfileDeactivated(String principal, DistrictUserProfilesView profile, String ipAddress) {
        return create(
                "%s deactivated %s's (%s) %s district user profile from IP address %s.",
                principal,
                profile.getUserName(),
                profile.getFullname(),
                profile.getDistrictName(),
                ipAddress
        );
    }

    public static ApplicationEvent districtProfileActivated(String principal, DistrictUserProfilesView profile, String ipAddress) {
        return create(
                "%s activated %s's (%s) %s district user profile from IP address %s.",
                principal,
                profile.getUserName(),
                profile.getFullname(),
                profile.getDistrictName(),
                ipAddress
        );
    }

    public static ApplicationEvent districtProfileDistrictChanged(
            String principal,
            DistrictUserProfilesView profile,
            Location newDistrict,
            String ipAddress) {
        return create(
                "%s moved %s'(%s) district user profile from %s to %s. Source ip address %s.",
                principal,
                profile.getFullname(),
                profile.getUserName(),
                profile.getDistrictName(),
                newDistrict.getName(),
                ipAddress
        );
    }
}
