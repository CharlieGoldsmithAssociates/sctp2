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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Immutable
@Table(name = "eligible_households_v")
public class EligibleHouseholdDetails {
    @Id
    @Column(name = "household_id")
    private Long householdId;

    @Column(name = "ml_code")
    private String mlCode;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "form_number")
    private Long formNumber;

    @Column(name = "members")
    private Long members;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "district_code")
    private Long districtCode;

    @Column(name = "ta", length = 100)
    private String ta;

    @Column(name = "ta_code")
    private Long taCode;

    @Column(name = "cluster", length = 100)
    private String cluster;

    @Column(name = "cluster_code")
    private Long clusterCode;

    @Column(name = "zone", length = 100)
    private String zone;

    @Column(name = "zone_code")
    private Long zoneCode;

    @Column(name = "village", length = 100)
    private String village;

    @Column(name = "village_code")
    private Long villageCode;

    @Column(name = "village_head", length = 200)
    private String villageHead;

    @Column(name = "household_head", length = 201)
    private String householdHead;

    @Column
    private Integer ranking;

    @Column(name = "last_cbt_ranking")
    private LocalDateTime lastRankingDate;

    @Column(name = "selection")
    @Convert(disableConversion = true)
    @Enumerated(EnumType.STRING)
    private CbtStatus selection;

    public Integer getRanking() {
        return ranking;
    }

    public LocalDateTime getLastRankingDate() {
        return lastRankingDate;
    }

    public CbtStatus getSelection() {
        return selection;
    }

    public String getMlCode() {
        return mlCode;
    }

    /*@Column(name = "member_details")
        @Type(type = "com.vladmihalcea.hibernate.type.json.JsonNodeStringType")
        @JsonSubTypes.Type(value = IndividualDetails.class)
        private JsonNode memberDetailsJson;

        public JsonNode getMemberDetailsJson() {
            return memberDetailsJson;
        }*/
    @Column(name = "member_details")
    @Convert(converter = HouseholdJsonMemberDataConverter.class)
    @JsonIgnore
    private List<IndividualDetails> memberDetails;

    public List<IndividualDetails> getMemberDetails() {
        return memberDetails;
    }

    public String getHouseholdHead() {
        return householdHead;
    }

    public String getVillageHead() {
        return villageHead;
    }

    public Long getVillageCode() {
        return villageCode;
    }

    public String getVillage() {
        return village;
    }

    public Long getZoneCode() {
        return zoneCode;
    }

    public String getZone() {
        return zone;
    }

    public Long getClusterCode() {
        return clusterCode;
    }

    public String getCluster() {
        return cluster;
    }

    public Long getTaCode() {
        return taCode;
    }

    public String getTa() {
        return ta;
    }

    public Long getDistrictCode() {
        return districtCode;
    }

    public String getDistrict() {
        return district;
    }

    public Long getMembers() {
        return members;
    }

    public Long getFormNumber() {
        return formNumber;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    protected EligibleHouseholdDetails() {
    }
}