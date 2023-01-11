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

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@MappedSuperclass
public class TransferBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long programId;

    @Column
    private Long householdId;

    @Column
    @Convert(converter = TransferStatus.Converter.class)
    private TransferStatus transferState;

    @Column
    private Long transferAgencyId;

    @Column
    private Long transferPeriodId;

    /**
     * Number of household Members during enrollment',
     */
    @Column
    private Integer householdMemberCount;

    /**
     * BIGINT COMMENT 'Amount to receive based on program basic amount or number of household members',
     */
    @Column
    private BigDecimal basicSubsidyAmount;

    /**
     * INT NOT NULL COMMENT 'Number of months in the transfer period',
     */
    @Column
    private Long numberOfMonths;

    /**
     * INT COMMENT 'Total number of children',
     */
    @Column
    private Long childrenCount;

    /**
     * INT COMMENT 'Number of children in primary school',
     */
    @Column
    private Long primaryChildrenCount;

    /**
     * INT COMMENT 'Number of children in primary school',
     */
    @Column
    private Long primaryIncentiveChildrenCount;

    /**
     * BIGINT COMMENT 'Amount to add based on number of primary going children',
     */
    @Column
    private BigDecimal primaryIncentiveAmount;

    /**
     * INT COMMENT 'Number of children in secondary education',
     */
    @Column
    private Long secondaryChildrenCount;

    /**
     * BIGINT COMMENT 'Amount to add based on number of primary going children',
     */
    @Column
    private BigDecimal primaryBonusAmount;

    /**
     * BIGINT COMMENT 'Amount to add based on number of primary going children',
     */
    @Column
    private BigDecimal secondaryBonusAmount;

    /**
     * TINYINT(1) DEFAULT 1 COMMENT 'Whether it is the first transfer for the household or not',
     */
    @Column(name = "is_first_transfer")
    private Long isFirstTransfer;

    /**
     * TINYINT(1) COMMENT 'Whether the transfer has been Suspended for other reasons',
     */
    @Column(name = "is_suspended")
    private Boolean isSuspended;

    /**
     * TINYINT(1) COMMENT 'Whether the transfer has been withheld because of case management issues',
     */
    @Column
    private Boolean isWithheld;

    /**
     * VARCHAR(50) NULL COMMENT 'Account number assigned for transfer',
     */
    @Column
    private String accountNumber;

    /**
     * BIGINT DEFAULT 0 COMMENT 'Amount received by the household',
     */
    @Column
    private BigDecimal amountDisbursed;

    /**
     * TINYINT(1) DEFAULT 0 COMMENT 'Whether the amount was disbursed/delivered to the household',
     */
    @Column(name = "is_collected")
    private Boolean isCollected;

    /**
     * DATE COMMENT 'When the amount was disbursed',
     */
    @Column
    private LocalDateTime disbursementDate;

    /**
     * BIGINT null COMMENT 'Amount that is pending from this transfer',
     */
    @Column
    private BigDecimal arrearsAmount;

    /**
     * BIGINT NULL COMMENT 'User who disbursed the amount for Manual transfers',
     */
    @Column
    private Long disbursedByUserId;

    /**
     * BIGINT NULL COMMENT 'Comment by disbursedByUser',
     */
    @Column(name = "comment", nullable = true, length = 200)
    private String comment;

    /**
     * BIGINT COMMENT 'TODO: Review and create topup_events table which will describe why topup exists',
     */
    @Column
    private Long topupEventId;

    @Column
    private BigDecimal totalTransferAmount;

    /**
     * BIGINT COMMENT 'Amount to be disbursed for topup',
     */
    @Column
    private BigDecimal topupAmount;

    @Column(name = "is_reconciled")
    private Boolean isReconciled;

    // ENUM('Manual', 'Automated') COMMENT 'TODO: Review this field',
    @Column
    private String reconciliationMethod;

    @Column
    private ZonedDateTime dateReconciled;

    @Column
    private ZonedDateTime createdAt;

    @Column
    private ZonedDateTime modifiedAt;

    // BIGINT NOT NULL COMMENT 'The user who created/initiated this transfer record',
    @Column
    private Long createdBy;

    // BIGINT NOT NULL COMMENT 'The user who approved/reviewed the transfer record should not be == created_by',
    @Column
    private Long reviewedBy;

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

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public TransferStatus getTransferState() {
        return transferState;
    }

    public void setTransferState(TransferStatus transferState) {
        this.transferState = transferState;
    }

    public Long getTransferAgencyId() {
        return transferAgencyId;
    }

    public void setTransferAgencyId(Long transferAgencyId) {
        this.transferAgencyId = transferAgencyId;
    }

    public Long getTransferPeriodId() {
        return transferPeriodId;
    }

    public void setTransferPeriodId(Long transferPeriodId) {
        this.transferPeriodId = transferPeriodId;
    }

    public Integer getHouseholdMemberCount() {
        return householdMemberCount;
    }

    public void setHouseholdMemberCount(Integer householdMemberCount) {
        this.householdMemberCount = householdMemberCount;
    }

    public BigDecimal getBasicSubsidyAmount() {
        return basicSubsidyAmount;
    }

    public void setBasicSubsidyAmount(BigDecimal basicSubsidyAmount) {
        this.basicSubsidyAmount = basicSubsidyAmount;
    }

    public Long getNumberOfMonths() {
        return numberOfMonths;
    }

    public void setNumberOfMonths(Long numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    public Long getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Long childrenCount) {
        this.childrenCount = childrenCount;
    }

    public Long getPrimaryChildrenCount() {
        return primaryChildrenCount;
    }

    public void setPrimaryChildrenCount(Long primaryChildrenCount) {
        this.primaryChildrenCount = primaryChildrenCount;
    }

    public Long getPrimaryIncentiveChildrenCount() {
        return primaryIncentiveChildrenCount;
    }

    public void setPrimaryIncentiveChildrenCount(Long primaryIncentiveChildrenCount) {
        this.primaryIncentiveChildrenCount = primaryIncentiveChildrenCount;
    }

    public BigDecimal getPrimaryIncentiveAmount() {
        return primaryIncentiveAmount;
    }

    public void setPrimaryIncentiveAmount(BigDecimal primaryIncentiveAmount) {
        this.primaryIncentiveAmount = primaryIncentiveAmount;
    }

    public Long getSecondaryChildrenCount() {
        return secondaryChildrenCount;
    }

    public void setSecondaryChildrenCount(Long secondaryChildrenCount) {
        this.secondaryChildrenCount = secondaryChildrenCount;
    }

    public BigDecimal getPrimaryBonusAmount() {
        return primaryBonusAmount;
    }

    public void setPrimaryBonusAmount(BigDecimal primaryBonusAmount) {
        this.primaryBonusAmount = primaryBonusAmount;
    }

    public BigDecimal getSecondaryBonusAmount() {
        return secondaryBonusAmount;
    }

    public void setSecondaryBonusAmount(BigDecimal secondaryBonusAmount) {
        this.secondaryBonusAmount = secondaryBonusAmount;
    }

    public Long getIsFirstTransfer() {
        return isFirstTransfer;
    }

    public void setIsFirstTransfer(Long isFirstTransfer) {
        this.isFirstTransfer = isFirstTransfer;
    }

    public Boolean getSuspended() {
        return isSuspended;
    }

    public void setSuspended(Boolean suspended) {
        isSuspended = suspended;
    }

    public Boolean getWithheld() {
        return isWithheld;
    }

    public void setWithheld(Boolean withheld) {
        isWithheld = withheld;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmountDisbursed() {
        return amountDisbursed;
    }

    public void setAmountDisbursed(BigDecimal amountDisbursed) {
        this.amountDisbursed = amountDisbursed;
    }

    public Boolean getCollected() {
        return isCollected;
    }

    public void setCollected(Boolean collected) {
        isCollected = collected;
    }

    public LocalDateTime getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(LocalDateTime disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public BigDecimal getTotalTransferAmount() {
        return totalTransferAmount;
    }

    public void setTotalTransferAmount(BigDecimal totalTransferAmount) {
        this.totalTransferAmount = totalTransferAmount;
    }

    public BigDecimal getArrearsAmount() {
        return arrearsAmount;
    }

    public void setArrearsAmount(BigDecimal arrearsAmount) {
        this.arrearsAmount = arrearsAmount;
    }

    public Long getDisbursedByUserId() {
        return disbursedByUserId;
    }

    public void setDisbursedByUserId(Long disbursedByUserId) {
        this.disbursedByUserId = disbursedByUserId;
    }

    public Long getTopupEventId() {
        return topupEventId;
    }

    public void setTopupEventId(Long topupEventId) {
        this.topupEventId = topupEventId;
    }

    public BigDecimal getTopupAmount() {
        return topupAmount;
    }

    public void setTopupAmount(BigDecimal topupAmount) {
        this.topupAmount = topupAmount;
    }

    public Boolean getReconciled() {
        return isReconciled;
    }

    public void setReconciled(Boolean reconciled) {
        isReconciled = reconciled;
    }

    public String getReconciliationMethod() {
        return reconciliationMethod;
    }

    public void setReconciliationMethod(String reconciliationMethod) {
        this.reconciliationMethod = reconciliationMethod;
    }

    public ZonedDateTime getDateReconciled() {
        return dateReconciled;
    }

    public void setDateReconciled(ZonedDateTime dateReconciled) {
        this.dateReconciled = dateReconciled;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal calculateMonthlyAmount() {
        return new BigDecimal("0.0")
                .add(this.basicSubsidyAmount)
                .add(this.secondaryBonusAmount)
                .add(this.primaryBonusAmount)
                .add(this.primaryIncentiveAmount);
    }

    public BigDecimal calculateTotalAmountToTransfer() {
        return this.calculateMonthlyAmount()
                .multiply(BigDecimal.valueOf(this.numberOfMonths))
                .add(this.topupAmount);
    }

}
