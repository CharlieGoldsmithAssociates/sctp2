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

package org.cga.sctp.transfers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TransferCalculationDto {
    private Long id;
    private Boolean isCollected;
    private Boolean isReconciled;
    private Boolean isExited;
    private Boolean isSuspended;
    private String formNumber;
    private Object householdHead;
    private Object mainReceiver;
    private Object alternateReceiver;
    private Integer numOfMembers;
    private Integer numOfChildren;
    private BigDecimal totalAmountWithoutArrears;
    private BigDecimal totalAmountWithArrears;
    private BigDecimal householdAmount;
    private BigDecimal primaryBonus;
    private BigDecimal primaryIncentive;
    private BigDecimal secondayBonus;
    private BigDecimal totalTopup;
    private BigDecimal totalMonthly;
    private BigDecimal arrearsAmount;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCollected() {
        return isCollected;
    }

    public void setCollected(Boolean collected) {
        isCollected = collected;
    }

    public Boolean getReconciled() {
        return isReconciled;
    }

    public void setReconciled(Boolean reconciled) {
        isReconciled = reconciled;
    }

    public Boolean getExited() {
        return isExited;
    }

    public void setExited(Boolean exited) {
        isExited = exited;
    }

    public Boolean getSuspended() {
        return isSuspended;
    }

    public void setSuspended(Boolean suspended) {
        isSuspended = suspended;
    }

    public String getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(String formNumber) {
        this.formNumber = formNumber;
    }

    public Object getHouseholdHead() {
        return householdHead;
    }

    public void setHouseholdHead(Object householdHead) {
        this.householdHead = householdHead;
    }

    public Object getMainReceiver() {
        return mainReceiver;
    }

    public void setMainReceiver(Object mainReceiver) {
        this.mainReceiver = mainReceiver;
    }

    public Object getAlternateReceiver() {
        return alternateReceiver;
    }

    public void setAlternateReceiver(Object alternateReceiver) {
        this.alternateReceiver = alternateReceiver;
    }

    public Integer getNumOfMembers() {
        return numOfMembers;
    }

    public void setNumOfMembers(Integer numOfMembers) {
        this.numOfMembers = numOfMembers;
    }

    public Integer getNumOfChildren() {
        return numOfChildren;
    }

    public void setNumOfChildren(Integer numOfChildren) {
        this.numOfChildren = numOfChildren;
    }

    public BigDecimal getTotalAmountWithoutArrears() {
        return totalAmountWithoutArrears;
    }

    public void setTotalAmountWithoutArrears(BigDecimal totalAmountWithoutArrears) {
        this.totalAmountWithoutArrears = totalAmountWithoutArrears;
    }

    public BigDecimal getTotalAmountWithArrears() {
        return totalAmountWithArrears;
    }

    public void setTotalAmountWithArrears(BigDecimal totalAmountWithArrears) {
        this.totalAmountWithArrears = totalAmountWithArrears;
    }

    public BigDecimal getHouseholdAmount() {
        return householdAmount;
    }

    public void setHouseholdAmount(BigDecimal householdAmount) {
        this.householdAmount = householdAmount;
    }

    public BigDecimal getPrimaryBonus() {
        return primaryBonus;
    }

    public void setPrimaryBonus(BigDecimal primaryBonus) {
        this.primaryBonus = primaryBonus;
    }

    public BigDecimal getPrimaryIncentive() {
        return primaryIncentive;
    }

    public void setPrimaryIncentive(BigDecimal primaryIncentive) {
        this.primaryIncentive = primaryIncentive;
    }

    public BigDecimal getSecondayBonus() {
        return secondayBonus;
    }

    public void setSecondayBonus(BigDecimal secondayBonus) {
        this.secondayBonus = secondayBonus;
    }

    public BigDecimal getTotalTopup() {
        return totalTopup;
    }

    public void setTotalTopup(BigDecimal totalTopup) {
        this.totalTopup = totalTopup;
    }

    public BigDecimal getTotalMonthly() {
        return totalMonthly;
    }

    public void setTotalMonthly(BigDecimal totalMonthly) {
        this.totalMonthly = totalMonthly;
    }

    public BigDecimal getArrearsAmount() {
        return arrearsAmount;
    }

    public void setArrearsAmount(BigDecimal arrearsAmount) {
        this.arrearsAmount = arrearsAmount;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
