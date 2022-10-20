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

import org.cga.sctp.targeting.enrollment.EnrollmentUpdateForm;
import org.cga.sctp.utils.LocaleUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

public class HouseholdRecipientsValidator implements ConstraintValidator<ValidRecipients, EnrollmentUpdateForm.HouseholdRecipients> {

    private ValidRecipients ctx;

    @Override
    public void initialize(ValidRecipients constraintAnnotation) {
        this.ctx = constraintAnnotation;
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(EnrollmentUpdateForm.HouseholdRecipients value, ConstraintValidatorContext context) {
        if (ctx.optional()) {
            return true;
        }
        if (value == null) {
            return false;
        }
        context.disableDefaultConstraintViolation();
        if (!ctx.primaryRecipientOptional() && value.getPrimaryMemberId() == null) {
            addConstraintViolation(context, "Missing primary receiver");
            return false;
        }
        if (value.getAlternateMemberId() != null && value.getOtherDetails() != null) {
            context.buildConstraintViolationWithTemplate(format("Alternate receiver information must either be a household member id or non-household member details."))
                    .addConstraintViolation();
            return false;
        }
        if (value.getAlternateMemberId() != null) {
            return true;
        }
        // validate details
        final EnrollmentUpdateForm.HouseholdRecipients.NonHouseholdMemberDetails details = value.getOtherDetails();
        if (details != null) {
            if (!LocaleUtils.checkLengthBounds(details.getFirstName(), 1, 30)) {
                addStringLengthViolation(context, "NonHouseholdMemberDetails.firstName", 1, 30);
                return false;
            }
            if (!LocaleUtils.checkLengthBounds(details.getLastName(), 1, 30)) {
                addStringLengthViolation(context, "NonHouseholdMemberDetails.lastName", 1, 30);
                return false;
            }
            if (LocaleUtils.isStringNullOrEmpty(details.getNationalId()) || !details.getNationalId().matches("^[0-9A-Za-z]{8}$")) {
                addConstraintViolation(context, "NonHouseholdMemberDetails.nationalId: Invalid format");
                return false;
            }
            if (details.getExpiryDate() == null) {
                addNotNullConstraintViolation(context, "NonHouseholdMemberDetails.expiryDate");
                return false;
            }
            if (details.getIssueDate() == null) {
                addNotNullConstraintViolation(context, "NonHouseholdMemberDetails.issueDate");
                return false;
            }
            if (details.getDateOfBirth() == null) {
                addNotNullConstraintViolation(context, "NonHouseholdMemberDetails.dateOfBirth");
                return false;
            }
        }
        return true;
    }

    private void addNotNullConstraintViolation(ConstraintValidatorContext ctx, String field) {
        addConstraintViolation(ctx, "%s: field is required.", field);
    }

    private void addStringLengthViolation(ConstraintValidatorContext ctx, String field, int min, int max) {
        addConstraintViolation(ctx, "%s: length must be between %d and %d characters.", field, min, max);
    }

    private void addConstraintViolation(ConstraintValidatorContext ctx, String message, Object... args) {
        ctx.buildConstraintViolationWithTemplate(format(message, args)).addConstraintViolation();
    }

    private static String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }
}
