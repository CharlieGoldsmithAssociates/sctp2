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

package org.cga.sctp.location;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Immutable
@Entity
@Table(name = "location_statuses")
public class LocationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long code;
    private String name;
    private Long parentCode;
    private Boolean targeting;
    private Boolean transfers;
    private Boolean enrollment;
    private Long enrollmentSessionId;
    @Enumerated(EnumType.STRING)
    private LocationType locationType;
    private Boolean districtValidation;
    private Boolean targetingSessionId;
    private Boolean transfersSessionId;
    private Boolean secondCommunityMeeting;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentCode() {
        return parentCode;
    }

    public void setParentCode(Long parentCode) {
        this.parentCode = parentCode;
    }

    public Boolean getTargeting() {
        return targeting;
    }

    public void setTargeting(Boolean targeting) {
        this.targeting = targeting;
    }

    public Boolean getTransfers() {
        return transfers;
    }

    public void setTransfers(Boolean transfers) {
        this.transfers = transfers;
    }

    public Boolean getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Boolean enrollment) {
        this.enrollment = enrollment;
    }

    public Long getEnrollmentSessionId() {
        return enrollmentSessionId;
    }

    public void setEnrollmentSessionId(Long enrollmentSessionId) {
        this.enrollmentSessionId = enrollmentSessionId;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Boolean getDistrictValidation() {
        return districtValidation;
    }

    public void setDistrictValidation(Boolean districtValidation) {
        this.districtValidation = districtValidation;
    }

    public Boolean getTargetingSessionId() {
        return targetingSessionId;
    }

    public void setTargetingSessionId(Boolean targetingSessionId) {
        this.targetingSessionId = targetingSessionId;
    }

    public Boolean getTransfersSessionId() {
        return transfersSessionId;
    }

    public void setTransfersSessionId(Boolean transfersSessionId) {
        this.transfersSessionId = transfersSessionId;
    }

    public Boolean getSecondCommunityMeeting() {
        return secondCommunityMeeting;
    }

    public void setSecondCommunityMeeting(Boolean secondCommunityMeeting) {
        this.secondCommunityMeeting = secondCommunityMeeting;
    }
}
