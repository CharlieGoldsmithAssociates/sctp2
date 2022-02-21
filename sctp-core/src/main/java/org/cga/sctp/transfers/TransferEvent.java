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

import org.cga.sctp.transfers.agencies.TransferMethod;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

//    private int ter_id;
//    private int numbermonthsperiod;
//    private int number;
//    private int consecutiveUnclaimed;
//    private int numberpaymnetnonrecertified;

@Entity
@Table(name = "transfers_events")
public class TransferEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // uuid?

    @Column
    private Long programId; // trarepenr_id;

    @Column
    private Long enrollmentSessionId; // trarepenr_id;

    @Column
    private Long transferSessionId; // trarepenr_id;

    @Column
    private Long householdId; // houmemenr_id

    @Column
    private Long recipientId; // houmemenr_id

    @Column
    private Long zoneId; // geo3_id

    @Column
    private Long villageClusterId; // geo4_id

    @Column
    private String accountNumber;

    @Column(name="first_transfer")
    private Boolean isFirstTransfer;

    @Column(name="collected")
    private Boolean isCollected;

    @Column(name = "transfer_received")
    private Boolean isTransferReceived; //flagreceivetransfer;

    @Column(name = "suspended")
    private Boolean isSuspended; //flagsuspended;

    @Column(name = "non_recertified")
    private Boolean isNonRecertified; //flagnonrecertified;

    @Column
    private TransferMethod modality; // modality

    @Column
    private Long subsidyAmount;// valuesubsidy;

    @Column
    private Long arrearsUncollectedAmount;// valuearrearsuncollected;

    @Column
    private Long arrearsUntransferredAmount;// valuearrearsuntransferred;

    @Column
    private Long arrearsUpdatedAmount;// valuearrearsupdated;

    @Column
    private Long arrearsAmount;// valuearrears;

    @Column
    private Long totalTransferAmount;// valuetotaltransfer;

    @Column
    private Long totalMembers;

    @Column
    private Long totalMembersPrimary;

    @Column
    private Long totalMembersPrimaryIncentive;

    @Column
    private Long totalMembersSecondary;

    @Column(name = "topup")
    private Boolean isTopup; // topup

    @Column
    private Long topupValue; //valuetopup;

    @Column
    private Long valueArrearsTopup;

    @Column
    private Long valueArrearstopupReceive;

    @Column
    private Boolean hasChangedGeolocation;

    @Column(name = "replaced")
    private Boolean isReplaced; // trarepenr_replaced;

    @Column(name = "transfer_field_work")
    private Boolean isTransferFieldWork; // istransferfieldwork

    @Column
    private LocalDateTime datefieldwork; // datefieldwork

//    private int fieldwork_houmemreceivespayment_id;
//    private String personauthorized;

    @Column(name="field_work_user_id")
    private Long fieldWorkUserID;// fieldwork_use_id;

    @Column(name = "upload_reconciliation")
    private Boolean isUploadReconciliation;

    @Column
    private LocalDate dateReconciled; // dateuploadreconciliation;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private TransferStatus transferStatus; // sta_id;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private TransferHouseholdState transferHouseholdState; // stahh_id;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getEnrollmentSessionId() {
        return enrollmentSessionId;
    }

    public void setEnrollmentSessionId(Long enrollmentSessionId) {
        this.enrollmentSessionId = enrollmentSessionId;
    }

    public Long getTransferSessionId() {
        return transferSessionId;
    }

    public void setTransferSessionId(Long transferSessionId) {
        this.transferSessionId = transferSessionId;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getVillageClusterId() {
        return villageClusterId;
    }

    public void setVillageClusterId(Long villageClusterId) {
        this.villageClusterId = villageClusterId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Boolean getFirstTransfer() {
        return isFirstTransfer;
    }

    public void setFirstTransfer(Boolean firstTransfer) {
        isFirstTransfer = firstTransfer;
    }

    public Boolean getCollected() {
        return isCollected;
    }

    public void setCollected(Boolean collected) {
        isCollected = collected;
    }

    public Boolean getTransferReceived() {
        return isTransferReceived;
    }

    public void setTransferReceived(Boolean transferReceived) {
        isTransferReceived = transferReceived;
    }

    public Boolean getSuspended() {
        return isSuspended;
    }

    public void setSuspended(Boolean suspended) {
        isSuspended = suspended;
    }

    public Boolean getNonRecertified() {
        return isNonRecertified;
    }

    public void setNonRecertified(Boolean nonRecertified) {
        isNonRecertified = nonRecertified;
    }

    public TransferMethod getModality() {
        return modality;
    }

    public void setModality(TransferMethod modality) {
        this.modality = modality;
    }

    public Long getSubsidyAmount() {
        return subsidyAmount;
    }

    public void setSubsidyAmount(Long subsidyAmount) {
        this.subsidyAmount = subsidyAmount;
    }

    public Long getArrearsUncollectedAmount() {
        return arrearsUncollectedAmount;
    }

    public void setArrearsUncollectedAmount(Long arrearsUncollectedAmount) {
        this.arrearsUncollectedAmount = arrearsUncollectedAmount;
    }

    public Long getArrearsUntransferredAmount() {
        return arrearsUntransferredAmount;
    }

    public void setArrearsUntransferredAmount(Long arrearsUntransferredAmount) {
        this.arrearsUntransferredAmount = arrearsUntransferredAmount;
    }

    public Long getArrearsUpdatedAmount() {
        return arrearsUpdatedAmount;
    }

    public void setArrearsUpdatedAmount(Long arrearsUpdatedAmount) {
        this.arrearsUpdatedAmount = arrearsUpdatedAmount;
    }

    public Long getArrearsAmount() {
        return arrearsAmount;
    }

    public void setArrearsAmount(Long arrearsAmount) {
        this.arrearsAmount = arrearsAmount;
    }

    public Long getTotalTransferAmount() {
        return totalTransferAmount;
    }

    public void setTotalTransferAmount(Long totalTransferAmount) {
        this.totalTransferAmount = totalTransferAmount;
    }

    public Long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public Long getTotalMembersPrimary() {
        return totalMembersPrimary;
    }

    public void setTotalMembersPrimary(Long totalMembersPrimary) {
        this.totalMembersPrimary = totalMembersPrimary;
    }

    public Long getTotalMembersPrimaryIncentive() {
        return totalMembersPrimaryIncentive;
    }

    public void setTotalMembersPrimaryIncentive(Long totalMembersPrimaryIncentive) {
        this.totalMembersPrimaryIncentive = totalMembersPrimaryIncentive;
    }

    public Long getTotalMembersSecondary() {
        return totalMembersSecondary;
    }

    public void setTotalMembersSecondary(Long totalMembersSecondary) {
        this.totalMembersSecondary = totalMembersSecondary;
    }

    public Boolean getTopup() {
        return isTopup;
    }

    public void setTopup(Boolean topup) {
        isTopup = topup;
    }

    public Long getTopupValue() {
        return topupValue;
    }

    public void setTopupValue(Long topupValue) {
        this.topupValue = topupValue;
    }

    public Long getValueArrearsTopup() {
        return valueArrearsTopup;
    }

    public void setValueArrearsTopup(Long valueArrearsTopup) {
        this.valueArrearsTopup = valueArrearsTopup;
    }

    public Long getValueArrearstopupReceive() {
        return valueArrearstopupReceive;
    }

    public void setValueArrearstopupReceive(Long valueArrearstopupReceive) {
        this.valueArrearstopupReceive = valueArrearstopupReceive;
    }

    public Boolean getHasChangedGeolocation() {
        return hasChangedGeolocation;
    }

    public void setHasChangedGeolocation(Boolean hasChangedGeolocation) {
        this.hasChangedGeolocation = hasChangedGeolocation;
    }

    public Boolean getReplaced() {
        return isReplaced;
    }

    public void setReplaced(Boolean replaced) {
        isReplaced = replaced;
    }

    public Boolean getTransferFieldWork() {
        return isTransferFieldWork;
    }

    public void setTransferFieldWork(Boolean transferFieldWork) {
        isTransferFieldWork = transferFieldWork;
    }

    public LocalDateTime getDatefieldwork() {
        return datefieldwork;
    }

    public void setDatefieldwork(LocalDateTime datefieldwork) {
        this.datefieldwork = datefieldwork;
    }

    public Long getFieldWorkUserID() {
        return fieldWorkUserID;
    }

    public void setFieldWorkUserID(Long fieldWorkUserID) {
        this.fieldWorkUserID = fieldWorkUserID;
    }

    public Boolean getUploadReconciliation() {
        return isUploadReconciliation;
    }

    public void setUploadReconciliation(Boolean uploadReconciliation) {
        isUploadReconciliation = uploadReconciliation;
    }

    public LocalDate getDateReconciled() {
        return dateReconciled;
    }

    public void setDateReconciled(LocalDate dateReconciled) {
        this.dateReconciled = dateReconciled;
    }

    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    public TransferHouseholdState getTransferHouseholdState() {
        return transferHouseholdState;
    }

    public void setTransferHouseholdState(TransferHouseholdState transferHouseholdState) {
        this.transferHouseholdState = transferHouseholdState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
