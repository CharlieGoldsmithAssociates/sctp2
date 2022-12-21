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

import org.cga.sctp.core.BaseComponent;
import org.cga.sctp.utils.LocaleUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class FilterTemplateValidator extends BaseComponent {
    public boolean validate(@NotNull TemplateValue value) {
        UnsupportedOperationException ex = new UnsupportedOperationException("unsupported field type");
        switch (value.getTemplate().getFieldType()) {
            case Number -> {
                if (!LocaleUtils.isNumber(value.getValue())) {
                    value.setErrorMessage("Value must be a valid positive number");
                    return false;
                }
            }
            case NumberSigned -> {
                if (!LocaleUtils.isSignedNumber(value.getValue())) {
                    value.setErrorMessage("Invalid number value");
                    return false;
                }
            }
            case Decimal -> {
                if (!LocaleUtils.isDecimalNumber(value.getValue())) {
                    value.setErrorMessage("Invalid decimal value");
                    return false;
                }
            }
            case Text -> {
                if (LocaleUtils.isStringNullOrEmpty(value.getValue())) {
                    value.setErrorMessage("Value is required");
                    return false;
                }
            }
            case ListSingle, ForeignMappedField -> {
                return true;
            }
            //case ListMultiple, NumberRange, DecimalRange, Date -> throw ex;
            default -> throw ex;
        }
        return true;
    }

    /**
     * <p>Returns a transformed value to be used. Some field types do not take input. This method provides the opportunity to filter unwanted input</p>
     *
     * @param template .
     * @param value    .
     * @return .
     */
    public String transformValue(CriteriaFilterTemplate template, String value) {
        if (template.getFieldType().wantsFilterValue) {
            return value;
        }
        return null;
    }
}
