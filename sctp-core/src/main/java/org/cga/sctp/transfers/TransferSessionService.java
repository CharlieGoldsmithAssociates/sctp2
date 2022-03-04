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
import org.cga.sctp.transfers.parameters.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferSessionService {

    @Autowired
    private TransferSessionRepository tranferSessionRepository;

    @Autowired
    private TransfersRepository transferRepository;

    @Autowired
    private HouseholdTransferParametersRepository householdTransferParametersRepository;

    @Autowired
    private LocationTransferParametersRepository locationTransferParametersRepository;

    @Autowired
    private EducationTransferParameterRepository educationTransferParameterRepository;

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
    @Transactional
    public void initiateTransfersForHouseholds(Long transferSessionId,
                                               Long enrollmentSessionId,
                                               Long programId,
                                               Long initiatedBy,
                                               List<Long> householdIds) {

        // transferSessionId
        transferRepository.initiateTransfersForEnrolledHouseholds(enrollmentSessionId, transferSessionId, initiatedBy);
//        for(Long householdId: householdIds) {
//            Transfer transfer = new Transfer();
//
//            transfer.setProgramId(null /* TODO */);
//            transfer.setEnrollmentSessionId(enrollmentSessionId);
//            transfer.setTransferSessionId(transferSessionId);
//            transfer.setHouseholdId(householdId);
//            transfer.setRecipientId(null /* TODO */);
//            transfer.setZoneId(null /* TODO */);
//            transfer.setVillageClusterId(null /* TODO */);
//            transfer.setAccountNumber(null /* TODO */);
//            transfer.setModality(TransferMethod.Manual); // until transfer agency is assigned that does EFT
//
//            transfer.setCreatedAt(LocalDateTime.now());
//            transfer.setModifiedAt(transfer.getCreatedAt());
//
//            transferRepository.save(transfer);
//        }
    }

    public List<TransferSessionDetailView> findAllActive(Pageable pageable) {
        return tranferSessionRepository.findAllActiveAsView(pageable.getPageNumber(), pageable.getPageSize());
    }

    public List<TransferEventHouseholdView> findAllHouseholdsInSession(Long sessionId) {
        return transferRepository.findAllHouseholdsByTransferSessionId(sessionId);
    }

    // TODO(zikani03) move this method to an appropriate service
    public List<HouseholdTransferParameter> findAllActiveHouseholdParameters() {
        return householdTransferParametersRepository.findAll();
    }

    // TODO(zikani03) move this method to an appropriate service
    public List<EducationTransferParameter> findAllEducationTransferParameters() {
        return educationTransferParameterRepository.findAll();
    }

    /**
     * Get the basic amount to be disbursed to the household according to it's size
     * @param householdSize number of members in the household
     * @return amount to give
     */
    public Long determineAmountByHouseholdSize(int householdSize) {
        List<HouseholdTransferParameter> householdParams = householdTransferParametersRepository.findAll();
        return determineAmountByHouseholdSize(householdSize, householdParams);
    }

    /**
     * Get the basic amount to be disbursed to the household according to it's size
     * @param householdSize size of the household
     * @param householdParams household params
     * @return amount
     */
    Long determineAmountByHouseholdSize(int householdSize, List<HouseholdTransferParameter> householdParams) {
        for(var param : householdParams) {
            if (param.getCondition().equals(HouseholdParameterCondition.GREATER_THAN) &&
                    householdSize > param.getNumberOfMembers()) {

                return param.getAmount();

            } else if (param.getCondition().equals(HouseholdParameterCondition.GREATER_THAN_OR_EQUAL) &&
                    householdSize >= param.getNumberOfMembers()) {

                return param.getAmount();
            } else if (param.getCondition().equals(HouseholdParameterCondition.EQUALS) &&
                    householdSize == param.getNumberOfMembers()) {

                return param.getAmount();
            }
        }

        return -1L;
    }

}

