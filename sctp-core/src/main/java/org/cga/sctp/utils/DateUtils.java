/*
 * Copyright (C) 2021 CGA Technologies, a trading name of Charlie Goldsmith Associates Ltd
 *  All rights reserved, released under the BSD-3 licence.
 *
 * CGA Technologies develop and use this software as part of its work
 *  but the software itself is open-source software; you can redistribute it and/or modify
 *  it under the terms of the BSD licence below
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
 *  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 * For more information please see http://opensource.org/licenses/BSD-3-Clause
 */

package org.cga.sctp.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Locale;

public final class DateUtils {
    public static String formatDateAsIsoString(LocalDateTime time) {
        return DateTimeFormatter.ISO_DATE_TIME.format(time);
    }

    public static String formatDateAsIsoString(LocalDate date) {
        return DateTimeFormatter.ofPattern("E, LLL d yyyy").withLocale(Locale.US).format(date);
    }

    public static String format(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("E, LLL d yyyy HH:mm").withLocale(Locale.US).format(dateTime);
    }

    public static boolean isDateAfter(LocalDate startDate, LocalDate endDate) {
        return startDate.isAfter(endDate);
    }

    public static long monthsBetweenInclusive(int startMonth, int startYear, int endMonth, int endYear) {
        return monthsBetween(startMonth, startYear, endMonth, endYear) + 1;
    }

    public static long monthsBetween(int startMonth, int startYear, int endMonth, int endYear) {
        LocalDate startDate = LocalDate.of(startYear, startMonth, 1),
                endDate = LocalDate.of(endYear, endMonth, 1);

        return monthsBetween(startDate, endDate);
    }

    public static long monthsBetween(LocalDate startDate, LocalDate endDate) {
        int startYear = startDate.getYear(), endYear = endDate.getYear();
        // one year is twelve months, 0 is twelve months
        int monthsSoFar = (endDate.getYear() - startDate.getYear()) * 12;
        if (startYear == endYear) {
            monthsSoFar = endDate.getMonthValue() - startDate.getMonthValue();
        } else {
            monthsSoFar += endDate.getMonthValue() - startDate.getMonthValue();
        }

        return monthsSoFar;
    }
}
