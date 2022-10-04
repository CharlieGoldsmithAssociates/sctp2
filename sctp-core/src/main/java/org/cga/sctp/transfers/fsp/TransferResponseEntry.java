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

package org.cga.sctp.transfers.fsp;

import java.math.BigDecimal;

public class TransferResponseEntry {
    /**
     * SCTP Code of the household
     */
    private String householdCode; //*

    /**
     * The code for the recipient who owns the account with the given account number
     */
    private String recipientCode; // *

    /**
     * The account number assigned by the Transfer Agency to the recipient
     */
    private String accountNumber; // *String

    /**
     * Code of the District the HH belongs to
     */
    private String districtCode; // *

    /**
     * The amount to transfer to the recipients account
     */
    private BigDecimal amount; // *

    /**
     * Actual amount disbursed to the beneficiary account
     */
    private BigDecimal amountDisbursed;//*

    /**
     * Status of the disbursement - this can be used to Indicate failed transfers due to reasons including closed/dormant accounts or system errors.
     */
    private String status; // *

    /**
     * When the transfer was made to this specific beneficiary account
     */
    private Long timestamp; // *

    /**
     * The ID of the transfer period the transfer event is performed under
     */
    private String transferPeriodID;

    /**
     * The Agency the Household is linked to in the period.
     */
    private String transferAgencyID;

    /**
     * Code of the Traditional Authority the HH belongs to
     */
    private String taCode;

    /**
     * Code of the Village Cluster the HH belongs to
     */
    private String villageClusterCode;

    /**
     * SCTP form-number of the household
     */
    private String householdFormNumber;

    public String getHouseholdCode() {
        return householdCode;
    }

    public void setHouseholdCode(String householdCode) {
        this.householdCode = householdCode;
    }

    public String getRecipientCode() {
        return recipientCode;
    }

    public void setRecipientCode(String recipientCode) {
        this.recipientCode = recipientCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountDisbursed() {
        return amountDisbursed;
    }

    public void setAmountDisbursed(BigDecimal amountDisbursed) {
        this.amountDisbursed = amountDisbursed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransferPeriodID() {
        return transferPeriodID;
    }

    public void setTransferPeriodID(String transferPeriodID) {
        this.transferPeriodID = transferPeriodID;
    }

    public String getTransferAgencyID() {
        return transferAgencyID;
    }

    public void setTransferAgencyID(String transferAgencyID) {
        this.transferAgencyID = transferAgencyID;
    }

    public String getTaCode() {
        return taCode;
    }

    public void setTaCode(String taCode) {
        this.taCode = taCode;
    }

    public String getVillageClusterCode() {
        return villageClusterCode;
    }

    public void setVillageClusterCode(String villageClusterCode) {
        this.villageClusterCode = villageClusterCode;
    }

    public String getHouseholdFormNumber() {
        return householdFormNumber;
    }

    public void setHouseholdFormNumber(String householdFormNumber) {
        this.householdFormNumber = householdFormNumber;
    }
}
