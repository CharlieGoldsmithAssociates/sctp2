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

package org.cga.sctp.targeting.importation.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GradeLevel implements UbrParameterValue {
    Standard1(1, "Std. 1"),
    Standard2(2, "Std. 2"),
    Standard3(3, "Std. 3"),
    Standard4(4, "Std. 4"),
    Standard5(5, "Std. 5"),
    Standard6(6, "Std. 6"),
    Standard7(7, "Std. 7"),
    Standard8(8, "Std. 8"),
    Form1(9, "Form 1"),
    Form2(10, "Form 2"),
    Form3(11, "Form 3"),
    Form4(12, "Form 4"),
    Other(13, null);

    public final int code;
    public final String text;
    public static final GradeLevel[] VALUES = values();

    GradeLevel(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GradeLevel fromCode(int code) {
        for (GradeLevel gradeLevel : VALUES) {
            if (gradeLevel.code == code) {
                return gradeLevel;
            }
        }
        throw new IllegalArgumentException("Invalid code");
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return text != null ? text : name();
    }
}
