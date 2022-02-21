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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferSessionService {

    @Autowired
    private TransferSessionRepository tranferSessionRepository;

    @Autowired
    private TransferEventRepository transferRepository;

    public TransferSessionRepository getTranferSessionRepository() {
        return tranferSessionRepository;
    }

    /**
     * Initiates Transfer Entries for households in the given session coming from a completed/closed enrollment.
     * The Transfer entries are created with default data for amounts, etc...
     *
     * @param transferSessionId session under which to perform the operations
     * @param householdIds the list of household ids assign transfers too
     * @param enrollmentSessionId the enrollment under which the households belong.
     */
    public void initiateTransfersForHouseholds(Long transferSessionId, List<Long> householdIds, Long enrollmentSessionId) {
        LoggerFactory.getLogger(getClass()).info("Initiating transfer session {} events for households {} in enrollment {}", transferSessionId, householdIds, enrollmentSessionId);

        for(Long householdId: householdIds) {
            TransferEvent transfer = new TransferEvent();

            transfer.setProgramId(null /* TODO */);
            transfer.setEnrollmentSessionId(enrollmentSessionId);
            transfer.setTransferSessionId(transferSessionId);
            transfer.setHouseholdId(householdId);
            transfer.setRecipientId(null /* TODO */);
            transfer.setZoneId(null /* TODO */);
            transfer.setVillageClusterId(null /* TODO */);
            transfer.setAccountNumber(null /* TODO */);
            transfer.setFirstTransfer(true);
            transfer.setCollected(false);
            transfer.setTransferReceived(false);
            transfer.setSuspended(false);
            transfer.setNonRecertified(false);
            transfer.setModality(TransferMethod.Manual); // until transfer agency is assigned that does EFT
            transfer.setSubsidyAmount(0L);
            transfer.setArrearsUncollectedAmount(0L);
            transfer.setArrearsUntransferredAmount(0L);
            transfer.setArrearsUpdatedAmount(0L);
            transfer.setArrearsAmount(0L);
            transfer.setTotalTransferAmount(0L);
            transfer.setTotalMembers(0L);
            transfer.setTotalMembersPrimary(0L);
            transfer.setTotalMembersPrimaryIncentive(0L);
            transfer.setTotalMembersSecondary(0L);
            transfer.setTopup(false);
            transfer.setTopupValue(0L);
            transfer.setValueArrearsTopup(0L);
            transfer.setValueArrearstopupReceive(0L);
            transfer.setHasChangedGeolocation(false);
            transfer.setReplaced(false);
            transfer.setTransferFieldWork(false);
            transfer.setDatefieldwork(null /* TODO */);
            transfer.setFieldWorkUserID(null /* TODO */);
            transfer.setUploadReconciliation(false);
            transfer.setDateReconciled(null /* TODO */);
            transfer.setTransferStatus(TransferStatus.Open);
            transfer.setTransferHouseholdState(TransferHouseholdState.ToTransfer);
            transfer.setCreatedAt(LocalDateTime.now());
            transfer.setModifiedAt(transfer.getCreatedAt());

            transferRepository.save(transfer);
        }
    }

    public List<TransferSessionDetailView> findAllActive(Pageable pageable) {
        return tranferSessionRepository.findAllActiveAsView(pageable.getPageNumber(), pageable.getPageSize());
    }

}

