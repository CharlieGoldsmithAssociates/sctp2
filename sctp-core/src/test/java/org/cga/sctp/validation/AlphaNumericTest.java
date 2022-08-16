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

package org.cga.sctp.validation;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AlphaNumericTest {
    private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    @Test
    public void testAlphaNumeric() {
        Entry e = new Entry();
        e.name = "@!@!@!##@!@!!@@!";

        Set<ConstraintViolation<Entry>> violationList = validatorFactory.getValidator().validate(e);

        assertFalse(violationList.isEmpty());

        e.name = "Some Name 9";

        violationList = validatorFactory.getValidator().validate(e);
        assertTrue(violationList.isEmpty());

        e.name = "";

        violationList = validatorFactory.getValidator().validate(e);
        assertEquals("Valid characters are: a-z, A-Z, 0-9, SPACE, and -.", Lists.newArrayList(violationList).get(0).getMessage());

        e.name = " ";
        violationList = validatorFactory.getValidator().validate(e);

        assertFalse(violationList.isEmpty());
        assertEquals("Value cannot be blank", Lists.newArrayList(violationList).get(0).getMessage());
    }

    static class Entry {
        @AlphaNumeric
        String name;
    }
}
