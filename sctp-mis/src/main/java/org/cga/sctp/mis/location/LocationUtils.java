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

package org.cga.sctp.mis.location;

import org.cga.sctp.location.HouseholdLocation;
import org.cga.sctp.location.LocationCode;
import org.cga.sctp.mis.core.templating.SelectOptionItem;

import java.util.List;
import java.util.stream.Collectors;

public final class LocationUtils {
    LocationUtils() {
    }

    public static List<SelectOptionItem> toSelectOptions(List<LocationCode> codes) {
        return codes.stream()
                .map(locationCode -> new SelectOptionItem(locationCode.getCode(),
                        locationCode.getName(), locationCode.getCode()))
                .collect(Collectors.toList());
    }

    public static List<SelectOptionItem> householdLocationsToSelectOptions(List<HouseholdLocation> locations) {
        return locations.stream()
                .map(location -> new SelectOptionItem(location.getCode(),
                        String.format("%s (%,d)", location.getName(), location.getHouseholdCount()), location.getCode()))
                .collect(Collectors.toList());
    }
}
