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

import java.util.List;

public class TransferRequest {
    /**
     * ID of the transfer period
     */
    private String transferPeriodID;

    /**
     * List of TransferRequestEntry
     * The list of Transfer request entries which represents a request to disburse funds into a beneficiary account managed by the Transfer Agency or FSP
     */
    private List<TransferRequestEntry> transferList;

    /**
     * The Timestamp when the process was initiated
     */
    private Long dateInitiated;

    /**
     * User who initiated the process on the Target MIS
     */
    private String userID;

    /**
     * System generated ID of the process
     */
    private String transactionID;

    public String getTransferPeriodID() {
        return transferPeriodID;
    }

    public void setTransferPeriodID(String transferPeriodID) {
        this.transferPeriodID = transferPeriodID;
    }

    public List<TransferRequestEntry> getTransferList() {
        return transferList;
    }

    public void setTransferList(List<TransferRequestEntry> transferList) {
        this.transferList = transferList;
    }

    public Long getDateInitiated() {
        return dateInitiated;
    }

    public void setDateInitiated(Long dateInitiated) {
        this.dateInitiated = dateInitiated;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }
}
