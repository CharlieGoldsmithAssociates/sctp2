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

package org.cga.sctp.transfers.periods;

import org.cga.sctp.utils.DateUtils;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "transfer_periods")
public class TransferPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Program cannot be null")
    @Column
    private Long programId;

    @Column
    private Long districtCode;

    @Column
    private long transferSessionId;

    @NotNull(message = "Transfer Period startDate cannot be null")
    @Column
    private LocalDate startDate;

    @NotNull(message = "Transfer Period endDate cannot be null")
    @Column
    private LocalDate endDate;

    @NotEmpty(message = "Transfer Period name cannot be null")
    @Column
    private String name;

    @NotEmpty(message = "Description cannot be null")
    @Column
    private String description;

    @Column
    private String villageClusterCodes;

    @Column
    private String traditionalAuthorityCodes;

    @Column
    private Long bonusPrimaryParameterId;

    @Column
    private Long bonusSecondaryParameterId;

    @NotNull(message = "Please specify whether period is open or closed")
    @Column
    private boolean closed;

    @NotNull(message = "Please specify user who opened the period")
    @Column
    private long openedBy;

    @Column
    @NotNull(message = "Created at must be specified")
    private ZonedDateTime createdAt;

    @Column
    @NotNull(message = "Updated at must be specified")
    private ZonedDateTime updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }

    public Long getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Long districtCode) {
        this.districtCode = districtCode;
    }

    public long getTransferSessionId() {
        return transferSessionId;
    }

    public void setTransferSessionId(long transferSessionId) {
        this.transferSessionId = transferSessionId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVillageClusterCodes() {
        return villageClusterCodes;
    }

    public void setVillageClusterCodes(String villageClusterCodes) {
        this.villageClusterCodes = villageClusterCodes;
    }

    public String getTraditionalAuthorityCodes() {
        return traditionalAuthorityCodes;
    }

    public void setTraditionalAuthorityCodes(String traditionalAuthorityCodes) {
        this.traditionalAuthorityCodes = traditionalAuthorityCodes;
    }

    public Long getBonusPrimaryParameterId() {
        return bonusPrimaryParameterId;
    }

    public void setBonusPrimaryParameterId(Long bonusPrimaryParameterId) {
        this.bonusPrimaryParameterId = bonusPrimaryParameterId;
    }

    public Long getBonusSecondaryParameterId() {
        return bonusSecondaryParameterId;
    }

    public void setBonusSecondaryParameterId(Long bonusSecondaryParameterId) {
        this.bonusSecondaryParameterId = bonusSecondaryParameterId;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public long getOpenedBy() {
        return openedBy;
    }

    public void setOpenedBy(long openedBy) {
        this.openedBy = openedBy;
    }

    public @NotNull(message = "Created at must be specified") ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NotNull(message = "Created at must be specified") ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public @NotNull(message = "Created at must be specified") ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@NotNull(message = "Created at must be specified") ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long countNoOfMonths() {
        return DateUtils.monthsBetween(this.startDate, this.endDate);
    }
}
