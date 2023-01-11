/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023, CGATechnologies
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

package org.cga.sctp.mis.transfers;

import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.transfers.TransferService;
import org.cga.sctp.transfers.TransferView;
import org.cga.sctp.transfers.TransferViewRepository;
import org.cga.sctp.transfers.agencies.TransferAgenciesRepository;
import org.cga.sctp.transfers.reconciliation.TransferReconciliationRequest;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;

@Controller
public class TransfersController extends SecuredBaseController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransferViewRepository transferViewRepository;
    @Autowired
    private TransferAgenciesRepository transferAgenciesRepository;

    @GetMapping("/transfers/pay/{transfer-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getPayView(@AuthenticatedUserDetails AuthenticatedUser user,
                                   @PathVariable("transfer-id") Long transferId) {

        var transferView = transferViewRepository.findById(transferId);

        if (transferView.isEmpty()) {
            return redirect("/transfers");
        }

        return view("transfers/pay_household")
                .addObject("transfer", transferView.get());
    }

    @PostMapping("/transfers/")
    @AdminAndStandardAccessOnly
    public ResponseEntity<TransferView> postUpdateAmount(@AuthenticatedUserDetails AuthenticatedUser user,
                                                         @PathVariable("transfer-id") Long transferId,
                                                         @RequestBody PayTransferDto payTransferDto) {

        var transferView = transferViewRepository.findById(transferId);

        if (transferView.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var request = new TransferReconciliationRequest();

        var transferReconciliation = new TransferReconciliationRequest.TransferReconciliation();

        transferReconciliation.setTimestamp(Timestamp.from(Instant.now()));
        transferReconciliation.setTransferAgencyId(transferView.get().getTransferAgencyId());
        transferReconciliation.setReconcilingUserId(user.id());
        transferReconciliation.setTransferId(transferId);
        transferReconciliation.setAmountTransferred(payTransferDto.getAmount());
        transferReconciliation.setComment(payTransferDto.getComment());

        request.setReconciliationList(Collections.singletonList(transferReconciliation));
        request.setTransferPeriodId(transferView.get().getTransferPeriodId());

        transferService.performManualTransfers(request, user.id());

        transferView = transferViewRepository.findById(transferId);
        // should we check presence of the transfer again?
        return transferView.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
