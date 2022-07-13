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
import javax.persistence.Table;

@Entity
@Immutable
@Table(name = "household_enrollment_v")
public class HouseholdEnrollmentView extends HouseholdEnrollmentBase {
    @Column(name = "ml_code")
    private Long mlCode;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "ta_name", nullable = false, length = 100)
    private String taName;

    @Column(name = "gvh_name", nullable = false, length = 100)
    private String gvhName;

    @Column(name = "cluster_name", nullable = false, length = 100)
    private String clusterName;

    @Column(name = "village_name", nullable = false, length = 100)
    private String villageName;

    private String zoneName;

    @Column(name = "member_count")
    private Long memberCount;

    @Column(name = "reviewed_by", length = 101)
    private String reviewedBy;

    private String householdHead;

    private Long formNumber;

    public Long getMlCode() {
        return mlCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getTaName() {
        return taName;
    }

    public String getGvhName() {
        return gvhName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getVillageName() {
        return villageName;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public String getHouseholdHead() {
        return householdHead;
    }

    public Long getFormNumber() {
        return formNumber;
    }

    public String getZoneName() {
        return zoneName;
    }
}