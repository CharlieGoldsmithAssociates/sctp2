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

package org.cga.sctp.beneficiaries;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cga.sctp.beneficiaries.converters.BrowsableHouseholdMemberDetailsConverter;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.cga.sctp.targeting.importation.parameters.RelationshipToHead;
import org.cga.sctp.utils.LocaleUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Locale;

@Entity
@Immutable
public class BrowsableHouseholdDetails {

    @Id
    @Column(name = "household_id")
    @JsonProperty("household_id")
    private Long householdId;

    @JsonProperty("form_number")
    @Column(name = "form_number")
    private Long formNumber;

    @JsonProperty("ml_code")
    @Column(name = "ml_code")
    private Long mlCode;

    @Column(name = "village_code")
    @JsonProperty("village_code")
    private Long villageCode;

    @JsonProperty("member_count")
    private Integer memberCount;

    @Convert(converter = BrowsableHouseholdMemberDetailsConverter.class)
    private List<BrowsableHouseholdDetails.HouseholdMemberDetails> members;

    public Long getHouseholdId() {
        return householdId;
    }

    public Long getFormNumber() {
        return formNumber;
    }

    public Long getMlCode() {
        return mlCode;
    }

    public Long getVillageCode() {
        return villageCode;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public List<HouseholdMemberDetails> getMembers() {
        return members;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public void setFormNumber(Long formNumber) {
        this.formNumber = formNumber;
    }

    public void setMlCode(Long mlCode) {
        this.mlCode = mlCode;
    }

    public void setVillageCode(Long villageCode) {
        this.villageCode = villageCode;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public void setMembers(List<HouseholdMemberDetails> members) {
        this.members = members;
    }

    public static class HouseholdMemberDetails {
        @JsonProperty
        private Long id;

        @JsonProperty
        private Locale dob;

        @JsonProperty
        private String name;

        @JsonProperty
        private Integer age;

        @JsonProperty
        private Gender gender;

        @JsonProperty("member_code")
        private String memberCode;

        @JsonProperty(value = "individual_id",access = JsonProperty.Access.WRITE_ONLY)
        private String individualId;

        @JsonProperty
        private RelationshipToHead rel;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Locale getDob() {
            return dob;
        }

        public void setDob(Locale dob) {
            this.dob = dob;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
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

        public String getIndividualId() {
            return individualId;
        }

        public void setIndividualId(String individualId) {
            this.individualId = individualId;
        }

        public RelationshipToHead getRel() {
            return rel;
        }

        public void setRel(RelationshipToHead rel) {
            this.rel = rel;
        }

        @JsonProperty("national_id")
        public String getNationalId() {
            return LocaleUtils.getNationalFromIndividualId(individualId);
        }
    }
}
