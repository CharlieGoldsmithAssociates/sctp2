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

package org.cga.sctp.targeting.enrollment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cga.sctp.targeting.enrollment.validators.ValidRecipients;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.cga.sctp.targeting.importation.parameters.GradeLevel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class EnrollmentUpdateForm {
    @NotNull(message = "List is enrollment data is required")
    @Size(min = 1, max = 1000, message = "List must not exceed {max} items")
    private List<@NotNull @Valid HouseholdEnrollment> householdEnrollment;

    public List<HouseholdEnrollment> getHouseholdEnrollment() {
        return householdEnrollment;
    }

    public void setHouseholdEnrollment(List<HouseholdEnrollment> householdEnrollment) {
        this.householdEnrollment = householdEnrollment;
    }

    /**
     * Allow only the following statuses from the API client
     */
    public enum EnrollmentStatus {
        Selected,
        // If a household originated straight from pre-eligibility verification
        Eligible,
        Enrolled
    }

    public static class HouseholdRecipients {
        public static class NonHouseholdMemberDetails {
            @NotNull
            private Gender gender;
            @NotNull
            private String lastName;
            @NotNull
            private String firstName;
            @NotNull
            @Pattern(regexp = "^[0-9A-Z]{8}$")
            private String nationalId;
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            private LocalDate issueDate;
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            private LocalDate expiryDate;
            @NotNull
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            private LocalDate dateOfBirth;

            public Gender getGender() {
                return gender;
            }

            public void setGender(Gender gender) {
                this.gender = gender;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getNationalId() {
                return nationalId;
            }

            public void setNationalId(String nationalId) {
                this.nationalId = nationalId;
            }

            public LocalDate getIssueDate() {
                return issueDate;
            }

            public void setIssueDate(LocalDate issueDate) {
                this.issueDate = issueDate;
            }

            public LocalDate getExpiryDate() {
                return expiryDate;
            }

            public void setExpiryDate(LocalDate expiryDate) {
                this.expiryDate = expiryDate;
            }

            public LocalDate getDateOfBirth() {
                return dateOfBirth;
            }

            public void setDateOfBirth(LocalDate dateOfBirth) {
                this.dateOfBirth = dateOfBirth;
            }
        }

        //@NotNull(message = "Primary member id is required")
        private Long primaryMemberId;

        /**
         * If null, {@link  #otherDetails} must contain details of non-household member
         */
        private Long alternateMemberId;
        private NonHouseholdMemberDetails otherDetails;

        public Long getPrimaryMemberId() {
            return primaryMemberId;
        }

        public void setPrimaryMemberId(Long primaryMemberId) {
            this.primaryMemberId = primaryMemberId;
        }

        public Long getAlternateMemberId() {
            return alternateMemberId;
        }

        public void setAlternateMemberId(Long alternateMemberId) {
            this.alternateMemberId = alternateMemberId;
        }

        public NonHouseholdMemberDetails getOtherDetails() {
            return otherDetails;
        }

        public void setOtherDetails(NonHouseholdMemberDetails otherDetails) {
            this.otherDetails = otherDetails;
        }
    }

    public static class SchoolEnrollment {
        @JsonIgnore
        private final String internalId;
        @NotNull
        private Integer schoolId;

        public SchoolEnrollment() {
            // used internally for binding query parameters
            this.internalId = UUID.randomUUID().toString().replace("-", "");
        }

        @NotNull
        private Boolean active;
        @NotNull
        private Long householdId;
        @NotNull
        private Long memberId;
        @NotNull
        private GradeLevel gradeLevel;
        @NotNull
        private EducationLevel educationLevel;

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public Long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Long householdId) {
            this.householdId = householdId;
        }

        public Long getMemberId() {
            return memberId;
        }

        public void setMemberId(Long memberId) {
            this.memberId = memberId;
        }

        public GradeLevel getGradeLevel() {
            return gradeLevel;
        }

        public void setGradeLevel(GradeLevel gradeLevel) {
            this.gradeLevel = gradeLevel;
        }

        public EducationLevel getEducationLevel() {
            return educationLevel;
        }

        public void setEducationLevel(EducationLevel educationLevel) {
            this.educationLevel = educationLevel;
        }

        public String getInternalId() {
            return internalId;
        }

        public Integer getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Integer schoolId) {
            this.schoolId = schoolId;
        }
    }

    public static class HouseholdEnrollment {
        @NotNull
        private Long householdId;
        @NotNull
        private EnrollmentStatus status;
        @NotNull
        @Size(max = 20)
        private List<@Valid @NotNull SchoolEnrollment> schoolEnrollment;

        @NotNull
        @ValidRecipients(primaryRecipientOptional = true, optional = true)
        private HouseholdRecipients recipients;

        public Long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Long householdId) {
            this.householdId = householdId;
        }

        public EnrollmentStatus getStatus() {
            return status;
        }

        public void setStatus(EnrollmentStatus status) {
            this.status = status;
        }

        public List<SchoolEnrollment> getSchoolEnrollment() {
            return schoolEnrollment;
        }

        public void setSchoolEnrollment(List<SchoolEnrollment> schoolEnrollment) {
            this.schoolEnrollment = schoolEnrollment;
        }

        public HouseholdRecipients getRecipients() {
            return recipients;
        }

        public void setRecipients(HouseholdRecipients recipients) {
            this.recipients = recipients;
        }
    }
}
