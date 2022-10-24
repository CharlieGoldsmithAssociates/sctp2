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

package org.cga.sctp.targeting.enrollment.validators;

import lib.gintec_rdl.spector.TypeInfo;
import org.cga.sctp.core.BaseComponent;
import org.cga.sctp.data.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class MultipartFileValidator extends BaseComponent implements ConstraintValidator<ValidFile, MultipartFile> {

    private ValidFile validFile;

    @Autowired
    private final ResourceService resourceService;

    public MultipartFileValidator(@Autowired ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.validFile = constraintAnnotation;
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (value == null || value.isEmpty()) {
            boolean valid = !validFile.required();
            if (validFile.required()) {
                context.buildConstraintViolationWithTemplate("this field is required")
                        .addPropertyNode(validFile.label())
                        .addConstraintViolation();
            }
            return valid;
        }
        final List<String> types = Arrays.asList(validFile.types());

        final ResourceService.FileInspectionResult result = resourceService.inspectFile(value, null);
        final TypeInfo typeInfo = result.getTypeInfo();

        if (typeInfo == null) {
            context.buildConstraintViolationWithTemplate("could not determine the file type")
                    .addPropertyNode(validFile.label())
                    .addConstraintViolation();
            return false;
        } else {
            if (!types.contains(typeInfo.getMime())) {
                context.buildConstraintViolationWithTemplate(format("invalid file type: %s. Allowed file types: %s", typeInfo.getMime(), types))
                        .addPropertyNode(validFile.label())
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
