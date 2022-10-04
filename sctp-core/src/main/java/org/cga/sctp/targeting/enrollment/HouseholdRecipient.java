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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "household_recipient")
public class HouseholdRecipient {
    @Id
    @Column(name = "household_id", nullable = false, unique = true)
    private Long householdId;
    private Long mainRecipient;
    private Long altRecipient;
    private String mainPhoto;
    private String mainPhotoType;
    private String altPhotoType;
    private Long enrollmentSession;
    private String altPhoto;
    private Long altOther;
    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public Long getMainRecipient() {
        return mainRecipient;
    }

    public void setMainRecipient(Long mainRecipient) {
        this.mainRecipient = mainRecipient;
    }

    public Long getAltRecipient() {
        return altRecipient;
    }

    public void setAltRecipient(Long altRecipient) {
        this.altRecipient = altRecipient;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(String mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public String getMainPhotoType() {
        return mainPhotoType;
    }

    public void setMainPhotoType(String mainPhotoType) {
        this.mainPhotoType = mainPhotoType;
    }

    public String getAltPhotoType() {
        return altPhotoType;
    }

    public void setAltPhotoType(String altPhotoType) {
        this.altPhotoType = altPhotoType;
    }

    public Long getEnrollmentSession() {
        return enrollmentSession;
    }

    public void setEnrollmentSession(Long enrollmentSession) {
        this.enrollmentSession = enrollmentSession;
    }

    public String getAltPhoto() {
        return altPhoto;
    }

    public void setAltPhoto(String altPhoto) {
        this.altPhoto = altPhoto;
    }

    public Long getAltOther() {
        return altOther;
    }

    public void setAltOther(Long altOther) {
        this.altOther = altOther;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(ZonedDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
