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
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Mapping for DB view
 */
@Entity
@Immutable
@Table(name = "household_imports")
public class HouseholdImport {
    @Id
    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "ml_code")
    private String mlCode;

    @Column(name = "error_count")
    private Integer errorCount;

    @Column(name = "form_number")
    private Long formNumber;

    @Column(name = "has_household_head")
    private Boolean hasHouseholdHead;

    @Column
    private Boolean archived;

    @Column(name = "member_count", nullable = false)
    private Long memberCount;

    @Column(name = "household_head_name")
    private String householdHeadName;

    @Column(name = "household_head_dob")
    private LocalDate householdHeadDob;

    @Column(name = "household_head_gender")
    @Convert(converter = GenderParameterValueConverter.class)
    private Gender householdHeadGender;

    @Column(name = "household_head_id")
    private String householdHeadId;

    public Boolean getArchived() {
        return archived;
    }

    @Column
    private String districtName;

    @Column
    private String clusterName;

    @Column
    private String villageName;

    @Column
    private String traditionalAuthorityName;

    @Column
    private String groupVillageHeadName;

    @Column(name = "data_import_id")
    private Long dataImportId;

    public Long getHouseholdId() {
        return householdId;
    }

    public Long getFormNumber() {
        return formNumber;
    }

    public Boolean getHasHouseholdHead() {
        return hasHouseholdHead;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public String getHouseholdHeadName() {
        return householdHeadName;
    }

    public LocalDate getHouseholdHeadDob() {
        return householdHeadDob;
    }

    public Gender getHouseholdHeadGender() {
        return householdHeadGender;
    }

    public String getHouseholdHeadId() {
        return householdHeadId;
    }

    public Long getDataImportId() {
        return dataImportId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getVillageName() {
        return villageName;
    }

    public String getTraditionalAuthorityName() {
        return traditionalAuthorityName;
    }

    public String getGroupVillageHeadName() {
        return groupVillageHeadName;
    }

    public String getMlCode() {
        return mlCode;
    }

    public Integer getErrorCount() {
        return errorCount;
    }
}