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

import org.cga.sctp.targeting.importation.converters.GenderParameterValueConverter;
import org.cga.sctp.targeting.importation.converters.RelationshipToHeadParameterValueConverter;
import org.cga.sctp.targeting.importation.parameters.RelationshipToHead;
import org.cga.sctp.targeting.importation.parameters.UbrParameterValue;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "household_recipient_candidates")
public class HouseholdRecipientCandidate {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 201)
    private String name;

    @Column(name = "individualId", nullable = false, length = 50)
    private String individualId;

    @Column(name = "dateOfBirth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "age")
    private Long age;

    @Convert(converter = GenderParameterValueConverter.class)
    @Column(name = "gender", nullable = false)
    private UbrParameterValue gender;

    @Column(name = "relationship")
    @Convert(converter = RelationshipToHeadParameterValueConverter.class)
    private RelationshipToHead relationship;

    @Column(name = "householdId")
    private Long householdId;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIndividualId() {
        return individualId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Long getAge() {
        return age;
    }

    public UbrParameterValue getGender() {
        return gender;
    }

    public RelationshipToHead getRelationship() {
        return relationship;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    protected HouseholdRecipientCandidate() {
    }
}