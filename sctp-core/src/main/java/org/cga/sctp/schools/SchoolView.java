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

package org.cga.sctp.schools;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cga.sctp.targeting.importation.converters.EducationLevelParameterValueConverter;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Immutable
@Table(name = "schools_view")
public class SchoolView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonProperty
    private Long id;

    @Column(name = "school_name")
    @JsonProperty("school_name")
    private String schoolName;

    @Column(name = "school_code")
    @JsonProperty("school_code")
    private String schoolCode;

    @Column(name = "education_level")
    @JsonProperty("education_level")
    @Convert(converter = EducationLevelParameterValueConverter.class)
    private EducationLevel educationLevel;

    @Column
    @JsonProperty
    private Boolean active;

    @Column(name = "ez_id")
    @JsonProperty("ez_id")
    private Long educationZoneId;

    @Column(name = "ez_code")
    @JsonProperty("ez_code")
    private Long educationZoneCode;

    @Column(name = "ez_name")
    @JsonProperty("ez_name")
    private String educationZoneName;

    @Column(name = "ez_district_code")
    @JsonProperty("ez_district_code")
    private Long educationZoneDistrictCode;

    @Column(name = "ez_district_name")
    @JsonProperty("ez_district_name")
    private String educationZoneDistrictName;

    @Column(name = "ez_ta_code")
    @JsonProperty("ez_ta_code")
    private Long educationZoneTaCode;

    @Column(name = "ez_ta_name")
    @JsonProperty("ez_ta_name")
    private String educationZoneTaName;

    public Long getId() {
        return id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public Boolean getActive() {
        return active;
    }

    public Long getEducationZoneId() {
        return educationZoneId;
    }

    public Long getEducationZoneCode() {
        return educationZoneCode;
    }

    public String getEducationZoneName() {
        return educationZoneName;
    }

    public Long getEducationZoneDistrictCode() {
        return educationZoneDistrictCode;
    }

    public String getEducationZoneDistrictName() {
        return educationZoneDistrictName;
    }

    public Long getEducationZoneTaCode() {
        return educationZoneTaCode;
    }

    public String getEducationZoneTaName() {
        return educationZoneTaName;
    }
}