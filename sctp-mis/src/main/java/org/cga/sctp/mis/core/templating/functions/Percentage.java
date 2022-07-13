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

package org.cga.sctp.mis.core.templating.functions;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.cga.sctp.mis.core.templating.PebbleFunctionImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Percentage extends PebbleFunctionImpl {
    private static final BigDecimal SCALE = BigDecimal.valueOf(100.00D);

    public Percentage() {
        super("percentage", List.of("dividend", "divisor"));
    }

    private BigDecimal max(Number left, Number right) {
        BigDecimal bdLeft = new BigDecimal(left.toString());
        BigDecimal bdRight = new BigDecimal(right.toString());
        int direction = bdLeft.compareTo(bdRight);
        if (direction >= 0) { // if they're at least equal, doesn't matter which one we return.
            return bdLeft;
        }
        return bdRight;
    }

    @Override
    public String execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        BigDecimal divisor = max(getArgument(args, "divisor", 1L), 1L);
        BigDecimal dividend = new BigDecimal(((Number) getArgument(args, "dividend", 0L)).toString());
        return String.format(Locale.US, "%d%%", dividend.divide(divisor, RoundingMode.HALF_UP).multiply(SCALE).toBigInteger());
    }
}
