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

package org.cga.sctp.targeting.importation.ubrapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * A request to the UBR API to fetch Households
 */
public class UbrRequest {
    /**
     * Status code for the SCTP Programme in the UBR API / Database - use this for most requests
     */
    public static final long UBR_SCTP_PROGRAMME_CODE = 1;

    @NonNull
    @JsonProperty("district_code")
    private String districtCode; //":"103",

    @NonNull
    @JsonProperty("traditional_authority_code")
    private String traditionalAuthorityCode; //": "10302",

    @JsonProperty("group_village_head_code")
    private String groupVillageHeadCode; //": "1030201",

    @Nullable
    @JsonProperty("village_code")
    private String villageCode; //": "1030201",

    @Nullable
    @JsonProperty("cluster_code")
    private String clusterCode;

    @Nullable
    @JsonProperty("zone_code")
    private String zoneCode;

    @Min(value = 0, message = "Lower Percentile Category cannot be less than 0")
    @Max(value = 90, message = "Lower Percentile Category must be less than or equal to 90")
    @JsonProperty("lower_percentile_category")
    private Long lowerPercentileCategory; //": "0",

    @Min(value = 10, message = "Upper Percentile Category must be greater than or equal to 10")
    @Max(value = 100, message = "Upper Percentile Category must be less than or equal to 100")
    @JsonProperty("upper_percentile_category")
    private Long upperPercentileCategory; //": "100",

    @JsonProperty("enrolment_status")
    private Long enrolmentStatus; //": 0,

    /**
     * See @link {@link org.cga.sctp.targeting.importation.parameters.WealthQuintile}
     */
    @Nullable
    @JsonProperty("wealth_quintiles")
    private String wealthClassification;

    @Nullable
    @JsonProperty("labour_constrained")
    private String labourConstrained;

    @Nullable
    @JsonProperty("dependency_ratio")
    private String dependencyRatio;

    @Nullable
    @JsonProperty("head_gender")
    private String headGender;

    @Nullable
    @JsonProperty("head_lower_age_limit")
    private Integer headLowerAgeLimit;

    @Nullable
    @JsonProperty("head_upper_age_limit")
    private Integer headUpperAgeLimit;

    @Nullable
    @JsonProperty("fit_for_work")
    private Integer fitForWork;

    @Nullable
    @JsonProperty("has_school_attending_member")
    private Integer hasSchoolAttendingMember;

    @Nullable
    @JsonProperty("has_member_with_disability")
    private Integer hasMemberWithDisability;

    @Nullable
    @JsonProperty("has_member_with_chronic_illness")
    private Integer hasMemberWithChronicIllness;

    @Nullable
    @JsonProperty("orphanhood")
    private String orphanhood;

    @NonNull
    @JsonProperty("programmes")
    private Long programmes; //": 1

    @Nullable
    @JsonProperty("enrolled_in_programmes")
    private String enrolledInProgrammes;

    @NonNull
    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(@NonNull String districtCode) {
        this.districtCode = districtCode;
    }

    @NonNull
    public String getTraditionalAuthorityCode() {
        return traditionalAuthorityCode;
    }

    public void setTraditionalAuthorityCode(@NonNull String traditionalAuthorityCode) {
        this.traditionalAuthorityCode = traditionalAuthorityCode;
    }

    public String getGroupVillageHeadCode() {
        return groupVillageHeadCode;
    }

    public void setGroupVillageHeadCode(String groupVillageHeadCode) {
        this.groupVillageHeadCode = groupVillageHeadCode;
    }

    @Nullable
    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(@Nullable String villageCode) {
        this.villageCode = villageCode;
    }

    @Nullable
    public String getClusterCode() {
        return clusterCode;
    }

    public void setClusterCode(@Nullable String clusterCode) {
        this.clusterCode = clusterCode;
    }

    @Nullable
    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(@Nullable String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public Long getLowerPercentileCategory() {
        return lowerPercentileCategory;
    }

    public void setLowerPercentileCategory(Long lowerPercentileCategory) {
        this.lowerPercentileCategory = lowerPercentileCategory;
    }

    public Long getUpperPercentileCategory() {
        return upperPercentileCategory;
    }

    public void setUpperPercentileCategory(Long upperPercentileCategory) {
        this.upperPercentileCategory = upperPercentileCategory;
    }

    public Long getEnrolmentStatus() {
        return enrolmentStatus;
    }

    public void setEnrolmentStatus(Long enrolmentStatus) {
        this.enrolmentStatus = enrolmentStatus;
    }

    @Nullable
    public String getWealthClassification() {
        return wealthClassification;
    }

    public void setWealthClassification(@Nullable String wealthClassification) {
        this.wealthClassification = wealthClassification;
    }

    @Nullable
    public String getLabourConstrained() {
        return labourConstrained;
    }

    public void setLabourConstrained(@Nullable String labourConstrained) {
        this.labourConstrained = labourConstrained;
    }

    @Nullable
    public String getDependencyRatio() {
        return dependencyRatio;
    }

    public void setDependencyRatio(@Nullable String dependencyRatio) {
        this.dependencyRatio = dependencyRatio;
    }

    @Nullable
    public String getHeadGender() {
        return headGender;
    }

    public void setHeadGender(@Nullable String headGender) {
        this.headGender = headGender;
    }

    @Nullable
    public Integer getHeadLowerAgeLimit() {
        return headLowerAgeLimit;
    }

    public void setHeadLowerAgeLimit(@Nullable Integer headLowerAgeLimit) {
        this.headLowerAgeLimit = headLowerAgeLimit;
    }

    @Nullable
    public Integer getHeadUpperAgeLimit() {
        return headUpperAgeLimit;
    }

    public void setHeadUpperAgeLimit(@Nullable Integer headUpperAgeLimit) {
        this.headUpperAgeLimit = headUpperAgeLimit;
    }

    @Nullable
    public Integer getFitForWork() {
        return fitForWork;
    }

    public void setFitForWork(@Nullable Integer fitForWork) {
        this.fitForWork = fitForWork;
    }

    @Nullable
    public Integer getHasSchoolAttendingMember() {
        return hasSchoolAttendingMember;
    }

    public void setHasSchoolAttendingMember(@Nullable Integer hasSchoolAttendingMember) {
        this.hasSchoolAttendingMember = hasSchoolAttendingMember;
    }

    @Nullable
    public Integer getHasMemberWithDisability() {
        return hasMemberWithDisability;
    }

    public void setHasMemberWithDisability(@Nullable Integer hasMemberWithDisability) {
        this.hasMemberWithDisability = hasMemberWithDisability;
    }

    @Nullable
    public Integer getHasMemberWithChronicIllness() {
        return hasMemberWithChronicIllness;
    }

    public void setHasMemberWithChronicIllness(@Nullable Integer hasMemberWithChronicIllness) {
        this.hasMemberWithChronicIllness = hasMemberWithChronicIllness;
    }

    @Nullable
    public String getOrphanhood() {
        return orphanhood;
    }

    public void setOrphanhood(@Nullable String orphanhood) {
        this.orphanhood = orphanhood;
    }

    @NonNull
    public Long getProgrammes() {
        return programmes;
    }

    public void setProgrammes(@NonNull Long programmes) {
        this.programmes = programmes;
    }

    @Nullable
    public String getEnrolledInProgrammes() {
        return enrolledInProgrammes;
    }

    public void setEnrolledInProgrammes(@Nullable String enrolledInProgrammes) {
        this.enrolledInProgrammes = enrolledInProgrammes;
    }
}
