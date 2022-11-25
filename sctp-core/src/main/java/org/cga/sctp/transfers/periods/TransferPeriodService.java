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

import org.cga.sctp.transfers.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransferPeriodService {
    @Autowired
    private TransferPeriodRepository transferPeriodRepository;

    @Autowired
    private TransferService transferService;

    /**
     * Creates a new transfer period if possible.
     *
     * The following preconditions must be met:
     *
     * * Transfer Agency must be assigned to the location the period is being created
     * * Previously opened periods must be closed before this one can be created.
     * * The Period must have start date after the end date of the previous period
     *
     * @param newPeriod
     * @return
     */
    @Transactional
    public TransferPeriod openNewPeriod(TransferPeriod newPeriod) throws TransferPeriodException {
        if (newPeriod.getStartDate().isAfter(newPeriod.getEndDate())) {
            throw new TransferPeriodException("Transfer Period duration is invalid");
        }
        // TODO: check for minimum duration of transfer
        // Check if we don't have another period already running
        // Check if the program is allowed to have a new period
        if (transferPeriodRepository.countAllOpenInProgramAndDistrict(newPeriod.getProgramId(), newPeriod.getDistrictCode()) > 0L) {
            throw new TransferPeriodException("Found an open Transfer Period in the district");
        }
        newPeriod.setClosed(false);
        newPeriod.setCreatedAt(LocalDateTime.now());
        newPeriod.setUpdatedAt(newPeriod.getCreatedAt());
        return transferPeriodRepository.save(newPeriod);
    }

    /**
     * Closes an open Transfer Period
     * @param transferPeriod the transfer period to close
     * @throws TransferPeriodException thrown if the period cannot be closed
     */
    public void closePeriod(@NonNull TransferPeriod transferPeriod) throws TransferPeriodException {
        if (transferPeriod.isClosed()) {
            return;
        }
        int pendingTransfers  = transferService.countUnreconciledTransfers(transferPeriod);
        if (pendingTransfers > 0) {
            throw new TransferPeriodException("Cannot close Transfer Period when some Transfers haven't been Closed/Reconciled");
        }
        transferPeriod.setClosed(true);
        // TODO: Generate statistics for this transfer period and store them somewhere
        transferPeriodRepository.save(transferPeriod);
    }

    public List<TransferPeriod> findAll() {
        return transferPeriodRepository.findAll();
    }

    public List<TransferPeriod> findAllOpen() {
        // TODO: implement me
        throw new UnsupportedOperationException("not yet implemented");
    }

    public List<TransferPeriod> findAllByDistrictCode(Long districtCode) {
        return transferPeriodRepository.findAllByDistrictCode(districtCode);
    }

    public Optional<TransferPeriod> findById(Long periodId) {
        return transferPeriodRepository.findById(periodId);
    }

    public void deletePeriod(Long periodId) {
        // TODO: validate that the period is closed...
        Optional<TransferPeriod> optional = transferPeriodRepository.findById(periodId);
        if (optional.isEmpty()) {
            return;
        }

        TransferPeriod period = optional.get();
        if (period.isClosed()) {
            // Cannot delete a closed period...
            return;
        }

        transferPeriodRepository.delete(period);
    }
}
