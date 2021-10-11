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

package org.cga.sctp.targeting.importation.validation;

import java.math.BigDecimal;

public final class LocationValidators {
    private static final BigDecimal LAT_MIN = new BigDecimal("-90.00000000");
    private static final BigDecimal LAT_MAX = new BigDecimal("90.00000000");
    private static final String LAT_ERROR_MSG = "Invalid GPS points: Latitude must be within -90 and +90 degrees.";

    private static final BigDecimal LON_MIN = new BigDecimal("-180.00000000");
    private static final BigDecimal LON_MAX = new BigDecimal("180.00000000");
    private static final String LON_ERROR_MSG = "Invalid GPS points: Longitude must be within -180 and +180 degrees.";

    public final static class LatitudeValidator extends NumberRangeValidator<BigDecimal> {
        public LatitudeValidator() {
            super(LAT_MIN, LAT_MAX, LAT_ERROR_MSG);
        }
    }

    public final static class LongitudeValidator extends NumberRangeValidator<BigDecimal> {
        public LongitudeValidator() {
            super(LON_MIN, LON_MAX, LON_ERROR_MSG);
        }
    }
}
