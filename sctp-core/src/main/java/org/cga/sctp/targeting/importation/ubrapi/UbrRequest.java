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

    @JsonProperty("district_code")
    private String districtCode; //":"103",
    
    @JsonProperty("traditional_authority_code")
    private String traditionalAuthorityCode; //": "10302",
    
    @JsonProperty("group_village_head_code")
    private String groupVillageHeadCode; //": "1030201",

    @Min(value=0,message = "Lower Percentile Category cannot be less than 0")
    @Max(value=90,message = "Lower Percentile Category must be less than or equal to 90")
    @JsonProperty("lower_percentile_category")
    private Long lowerPercentileCategory; //": "0",

    @Min(value=10,message = "Upper Percentile Category must be greater than or equal to 10")
    @Max(value=100,message = "Upper Percentile Category must be less than or equal to 100")
    @JsonProperty("upper_percentile_category")
    private Long upperPercentileCategory; //": "100",
    
    @JsonProperty("enrolment_status")
    private Long enrolmentStatus; //": 0,
    
    @JsonProperty("programmes")
    private Long programmes; //": 1


    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getTraditionalAuthorityCode() {
        return traditionalAuthorityCode;
    }

    public void setTraditionalAuthorityCode(String traditionalAuthorityCode) {
        this.traditionalAuthorityCode = traditionalAuthorityCode;
    }

    public String getGroupVillageHeadCode() {
        return groupVillageHeadCode;
    }

    public void setGroupVillageHeadCode(String groupVillageHeadCode) {
        this.groupVillageHeadCode = groupVillageHeadCode;
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

    public Long getProgrammes() {
        return programmes;
    }

    public void setProgrammes(Long programmes) {
        this.programmes = programmes;
    }
}
