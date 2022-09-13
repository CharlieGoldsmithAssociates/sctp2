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

package org.cga.sctp.targeting.exchange;

import org.cga.sctp.targeting.importation.converters.GenderParameterValueConverter;
import org.cga.sctp.targeting.importation.converters.RelationshipToHeadParameterValueConverter;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.cga.sctp.targeting.importation.parameters.RelationshipToHead;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Mapping for DB view
 */
@Entity
@Immutable
@Table(name = "household_member_imports")
public class HouseholdMemberImport {
    @Id
    @Column(name = "household_member_id")
    private Long householdMemberId;

    @Column(name = "data_import_id")
    private Long dataImportId;

    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "name", length = 101)
    private String name;

    @Column(name = "gender")
    @Convert(converter = GenderParameterValueConverter.class)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "national_id", length = 8)
    private String nationalId;

    @Column(name = "relationship")
    @Convert(converter = RelationshipToHeadParameterValueConverter.class)
    private RelationshipToHead relationship;

    public Long getHouseholdMemberId() {
        return householdMemberId;
    }

    public Long getDataImportId() {
        return dataImportId;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationalId() {
        return nationalId;
    }

    public RelationshipToHead getRelationship() {
        return relationship;
    }

    protected HouseholdMemberImport() {
    }
}