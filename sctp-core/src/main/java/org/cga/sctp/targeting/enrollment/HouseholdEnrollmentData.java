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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.cga.sctp.targeting.importation.parameters.GradeLevel;
import org.cga.sctp.targeting.importation.parameters.RelationshipToHead;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Immutable
@Table(name = "household_enrollment_data")
public class HouseholdEnrollmentData extends BaseHouseholdEnrollmentSummary {
    @Column
    @JsonProperty("primary_recipient")
    @Convert(converter = HouseholdRecipientJsonConverter.class)
    private HouseholdRecipientInfo primaryRecipient;

    @Column
    @JsonProperty("alternate_recipient")
    @Convert(converter = HouseholdRecipientJsonConverter.class)
    private HouseholdRecipientInfo alternateRecipient;

    @Column
    @Convert(converter = ChildSchoolEnrollmentConverter.class)
    @JsonProperty("school_enrollment")
    private List<ChildSchoolEnrollment> schoolEnrollment;

    @Column
    @Convert(converter = HouseholdMemberEnrollmentJsonConverter.class)
    @JsonProperty("household_members")
    private List<HouseholdMember> householdMembers;

    public HouseholdRecipientInfo getPrimaryRecipient() {
        return primaryRecipient;
    }

    public void setPrimaryRecipient(HouseholdRecipientInfo primaryRecipient) {
        this.primaryRecipient = primaryRecipient;
    }

    public HouseholdRecipientInfo getAlternateRecipient() {
        return alternateRecipient;
    }

    public void setAlternateRecipient(HouseholdRecipientInfo alternateRecipient) {
        this.alternateRecipient = alternateRecipient;
    }

    public List<ChildSchoolEnrollment> getSchoolEnrollment() {
        return schoolEnrollment;
    }

    public void setSchoolEnrollment(List<ChildSchoolEnrollment> schoolEnrollment) {
        this.schoolEnrollment = schoolEnrollment;
    }

    public List<HouseholdMember> getHouseholdMembers() {
        return householdMembers;
    }

    public void setHouseholdMembers(List<HouseholdMember> householdMembers) {
        this.householdMembers = householdMembers;
    }

    public static class HouseholdRecipientInfo {
        @JsonProperty("member_id")
        private Long memberId;
        private Gender gender;
        @JsonProperty("last_name")
        private String lastName;
        @JsonProperty("household_id")
        private Long householdId;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("member_code")
        private String memberCode;
        @JsonProperty("individual_id")
        private String individualId;
        @JsonProperty("date_of_birth")
        private LocalDate dateOfBirth;
        @JsonProperty("is_household_member")
        private Boolean isHouseholdMember;

        public Long getMemberId() {
            return memberId;
        }

        public void setMemberId(Long memberId) {
            this.memberId = memberId;
        }

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

        public Long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Long householdId) {
            this.householdId = householdId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMemberCode() {
            return memberCode;
        }

        public void setMemberCode(String memberCode) {
            this.memberCode = memberCode;
        }

        public String getIndividualId() {
            return individualId;
        }

        public void setIndividualId(String individualId) {
            this.individualId = individualId;
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        @JsonIgnore
        public Boolean getHouseholdMember() {
            return isHouseholdMember;
        }

        public void setHouseholdMember(Boolean householdMember) {
            isHouseholdMember = householdMember;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HouseholdRecipientInfo that = (HouseholdRecipientInfo) o;

            if (!memberId.equals(that.memberId)) return false;
            return householdId.equals(that.householdId);
        }

        @Override
        public int hashCode() {
            int result = memberId.hashCode();
            result = 31 * result + householdId.hashCode();
            return result;
        }
    }

    public static class ChildSchoolEnrollment {
        private Long id;
        @JsonProperty("member_id")
        private Long memberId;
        private Boolean status;
        @JsonProperty("school_id")
        private Integer schoolId;
        @JsonProperty("household_id")
        private Long householdId;
        private GradeLevel grade;
        @JsonProperty("education_level")
        private EducationLevel educationLevel;

        @JsonIgnore
        private String internalId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getMemberId() {
            return memberId;
        }

        public void setMemberId(Long memberId) {
            this.memberId = memberId;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public Integer getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Integer schoolId) {
            this.schoolId = schoolId;
        }

        public Long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Long householdId) {
            this.householdId = householdId;
        }

        public GradeLevel getGrade() {
            return grade;
        }

        public void setGrade(GradeLevel grade) {
            this.grade = grade;
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

        public void setInternalId(String internalId) {
            this.internalId = internalId;
        }
    }

    public static class HouseholdMember {
        @JsonProperty("member_id")
        private Long memberId;
        @JsonProperty("gender")
        private Gender gender;
        @JsonProperty("member_code")
        private String memberCode;
        @JsonProperty("last_name")
        private String lastName;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("household_id")
        private Long householdId;
        @JsonProperty("household_code")
        private Long householdCode;
        @JsonProperty("date_of_birth")
        private String dateOfBirth;
        @JsonProperty("individual_id")
        private String individualId;
        @JsonProperty("id_issue_date")
        private LocalDate idIssueDate;
        @JsonProperty("id_expiry_date")
        private LocalDate idExpiryDate;
        @JsonProperty("relationship")
        private RelationshipToHead relationship;

        public Long getMemberId() {
            return memberId;
        }

        public void setMemberId(Long memberId) {
            this.memberId = memberId;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getMemberCode() {
            return memberCode;
        }

        public void setMemberCode(String memberCode) {
            this.memberCode = memberCode;
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

        public Long getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(Long householdId) {
            this.householdId = householdId;
        }

        public Long getHouseholdCode() {
            return householdCode;
        }

        public void setHouseholdCode(Long householdCode) {
            this.householdCode = householdCode;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getIndividualId() {
            return individualId;
        }

        public void setIndividualId(String individualId) {
            this.individualId = individualId;
        }

        public LocalDate getIdIssueDate() {
            return idIssueDate;
        }

        public void setIdIssueDate(LocalDate idIssueDate) {
            this.idIssueDate = idIssueDate;
        }

        public LocalDate getIdExpiryDate() {
            return idExpiryDate;
        }

        public void setIdExpiryDate(LocalDate idExpiryDate) {
            this.idExpiryDate = idExpiryDate;
        }

        public RelationshipToHead getRelationship() {
            return relationship;
        }

        public void setRelationship(RelationshipToHead relationship) {
            this.relationship = relationship;
        }
    }
}
