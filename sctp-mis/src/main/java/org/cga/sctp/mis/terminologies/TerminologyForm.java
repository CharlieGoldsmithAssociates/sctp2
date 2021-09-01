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

package org.cga.sctp.mis.terminologies;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class TerminologyForm {
    @NotBlank(message = "Subnational 1 cannot be empty")
    @Length(min = 1, max = 50, message = "Must have {min}-{max} characters")
    private String subnational1;

    @NotBlank(message = "Subnational 2 cannot be empty")
    @Length(min = 1, max = 50, message = "Must have {min}-{max} characters")
    private String subnational2;

    @NotBlank(message = "Subnational 3 cannot be empty")
    @Length(min = 1, max = 50, message = "Must have {min}-{max} characters")
    private String subnational3;

    @NotBlank(message = "Subnational 4 cannot be empty")
    @Length(min = 1, max = 50, message = "Must have {min}-{max} characters")
    private String subnational4;

    @NotBlank(message = "Subnational 5 cannot be empty")
    @Length(min = 1, max = 50, message = "Must have {min}-{max} characters")
    private String subnational5;

    public String getSubnational1() {
        return subnational1;
    }

    public void setSubnational1(String subnational1) {
        this.subnational1 = subnational1;
    }

    public String getSubnational2() {
        return subnational2;
    }

    public void setSubnational2(String subnational2) {
        this.subnational2 = subnational2;
    }

    public String getSubnational3() {
        return subnational3;
    }

    public void setSubnational3(String subnational3) {
        this.subnational3 = subnational3;
    }

    public String getSubnational4() {
        return subnational4;
    }

    public void setSubnational4(String subnational4) {
        this.subnational4 = subnational4;
    }

    public String getSubnational5() {
        return subnational5;
    }

    public void setSubnational5(String subnational5) {
        this.subnational5 = subnational5;
    }
}
