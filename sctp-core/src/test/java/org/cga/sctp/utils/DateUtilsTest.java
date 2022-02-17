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

package org.cga.sctp.utils;

import org.junit.jupiter.api.Test;

import static org.cga.sctp.utils.DateUtils.monthsBetween;
import static org.cga.sctp.utils.DateUtils.monthsBetweenInclusive;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {
    @Test
    public void testMonthsBetweenExclusive() {
        assertEquals(2, monthsBetween(1, 2022, 3, 2022));

        assertEquals(11, monthsBetween(1,2022, 12, 2022));

        assertEquals(12, monthsBetween(1, 2022, 1, 2023));

        assertEquals(23, monthsBetween(1, 2022, 12, 2023));
    }

    @Test
    public void testMonthsBetweenInclusive() {
        assertEquals(3, monthsBetweenInclusive(1, 2022, 3, 2022));

        assertEquals(12, monthsBetweenInclusive(1,2022, 12, 2022));

        assertEquals(13, monthsBetweenInclusive(1, 2022, 1, 2023));

        assertEquals(24, monthsBetweenInclusive(1, 2022, 12, 2023));
    }
}