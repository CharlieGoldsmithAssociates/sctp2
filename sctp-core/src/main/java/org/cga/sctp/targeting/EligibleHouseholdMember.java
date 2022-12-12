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

package org.cga.sctp.targeting;

import org.cga.sctp.targeting.importation.converters.ChronicIllnessParameterValueConverter;
import org.cga.sctp.targeting.importation.converters.DisabilityParameterValueConverter;
import org.cga.sctp.targeting.importation.converters.GenderParameterValueConverter;
import org.cga.sctp.targeting.importation.converters.RelationshipToHeadParameterValueConverter;
import org.cga.sctp.targeting.importation.parameters.ChronicIllness;
import org.cga.sctp.targeting.importation.parameters.Disability;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.cga.sctp.targeting.importation.parameters.RelationshipToHead;
import org.hibernate.annotations.Immutable;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity(name = "eligible_household_members_view")
@Immutable
public class EligibleHouseholdMember {

    @Id
    private Long id;
    private Long sessionId;
    private Long householdCode;
    private Long formNumber;
    private String name;
    private String memberCode;

    @Convert(converter = GenderParameterValueConverter.class)
    private Gender gender;

    @Convert(converter = RelationshipToHeadParameterValueConverter.class)
    private RelationshipToHead relationship;
    private Integer age;

    @Convert(converter = DisabilityParameterValueConverter.class)
    private Disability disability;
    private String nationalId;

    @Convert(converter = ChronicIllnessParameterValueConverter.class)
    private ChronicIllness chronicIllness;

    private String district;
    private String ta;
    private String cluster;
    private String zone;
    private String village;
    private LocalDate dateOfBirth;

    public Long getId() {
        return id;
    }

    public Long getHouseholdCode() {
        return householdCode;
    }

    public Long getFormNumber() {
        return formNumber;
    }

    public String getName() {
        return name;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }


    public Gender getGender() {
        return gender;
    }

    public RelationshipToHead getRelationship() {
        return relationship;
    }

    public String getNationalId() {
        return nationalId;
    }

    public Disability getDisability() {
        return disability;
    }

    public ChronicIllness getChronicIllness() {
        return chronicIllness;
    }

    public String getDistrict() {
        return district;
    }

    public String getTa() {
        return ta;
    }

    public String getCluster() {
        return cluster;
    }

    public String getZone() {
        return zone;
    }

    public String getVillage() {
        return village;
    }

    public Long getSessionId() {
        return sessionId;
    }
}
