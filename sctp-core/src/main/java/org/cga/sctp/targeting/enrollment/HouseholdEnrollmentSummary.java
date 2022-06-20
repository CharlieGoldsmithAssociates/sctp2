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

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Immutable
@Table(name = "household_enrollment_summary")
public class HouseholdEnrollmentSummary {
    @Id
    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "form_number", nullable = false)
    private Long formNumber;

    @Column(name = "ml_code", nullable = false)
    private Long mlCode;

    @Column(name = "district_code", nullable = false)
    private Long districtCode;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "ta_code", nullable = false, length = 100)
    private Long taCode;

    @Column(name = "ta_name", nullable = false, length = 100)
    private String taName;

    @Column(name = "gvh_code", nullable = false)
    private Long gvhCode;

    @Column(name = "group_village_head", nullable = false, length = 100)
    private String groupVillageHead;

    @Column(name = "cluster_code", nullable = false)
    private Long clusterCode;

    @Column(name = "cluster_name", nullable = false, length = 100)
    private String clusterName;

    @Column(name = "zone_code", nullable = false)
    private Long zoneCode;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    @Column(name = "village_code", nullable = false)
    private Long villageCode;

    @Column(name = "village_name", nullable = false, length = 100)
    private String villageName;

    @Column(name = "household_head", length = 201)
    private String householdHead;

    @Column(name = "individual_id", length = 50)
    private String individualId;

    @Column(name = "member_count")
    private Long memberCount;

    @Column(name = "child_enrollment6to15")
    private Long childEnrollment6to15;

    @Column(name = "primary_children")
    private Long primaryChildren;

    @Column(name = "secondary_children")
    private Long secondaryChildren;

    public Long getHouseholdId() {
        return householdId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getFormNumber() {
        return formNumber;
    }

    public Long getMlCode() {
        return mlCode;
    }

    public Long getDistrictCode() {
        return districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public Long getGvhCode() {
        return gvhCode;
    }

    public Long getTaCode() {
        return taCode;
    }

    public String getTaName() {
        return taName;
    }

    public String getGroupVillageHead() {
        return groupVillageHead;
    }

    public Long getClusterCode() {
        return clusterCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    public Long getZoneCode() {
        return zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public Long getVillageCode() {
        return villageCode;
    }

    public String getVillageName() {
        return villageName;
    }

    public String getHouseholdHead() {
        return householdHead;
    }

    public String getIndividualId() {
        return individualId;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public Long getChildEnrollment6to15() {
        return childEnrollment6to15;
    }

    public Long getPrimaryChildren() {
        return primaryChildren;
    }

    public Long getSecondaryChildren() {
        return secondaryChildren;
    }

    public HouseholdEnrollmentSummary() {
    }
}