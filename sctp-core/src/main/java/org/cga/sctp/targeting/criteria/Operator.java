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

package org.cga.sctp.targeting.criteria;

import java.util.Locale;

/**
 * <p>Used for building SQL queries that have multiple ranged options</p>
 */
public enum Operator {
    EQUALS(false),
    NOT_EQUALS(false),
    GREATER_THAN(false),
    LESS_THAN(false),
    GREATER_THAN_OR_EQUAL_TO(false),
    LESS_THAN_OR_EQUAL_TO(false),
    BETWEEN(true);

    Operator(boolean isRanged) {
        this.isRanged = isRanged;
    }

    /**
     * <p>Indicates whether this operator is ranged, i.e, requires two numbers to complete the comparison</p>
     */
    public final boolean isRanged;

    public <T> String buildCondition(String column, String placeholder) {
        return switch (this) {
            case EQUALS -> format(" (%s = :%s)", column, placeholder);
            case NOT_EQUALS -> format(" (%s != :%s)", column, placeholder);
            case GREATER_THAN -> format(" (%s > :%s)", column, placeholder);
            case LESS_THAN -> format(" (%s < :%s)", column, placeholder);
            case GREATER_THAN_OR_EQUAL_TO -> format(" (%s >= :%s)", column, placeholder);
            case LESS_THAN_OR_EQUAL_TO -> format(" (%s <= :%s)", column, placeholder);
            case BETWEEN -> format(" (%1$s BETWEEN :%2$s_1 AND :%2$s_2)", column, placeholder);
        };
    }

    private String format(String fmt, Object... args) {
        return String.format(Locale.US, fmt, args);
    }
}
