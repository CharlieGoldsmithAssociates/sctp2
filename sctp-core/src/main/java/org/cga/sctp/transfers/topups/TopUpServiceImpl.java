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

package org.cga.sctp.transfers.topups;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TopUpServiceImpl implements TopUpService {
    @Autowired
    private TopUpRepository topUpRepository;

    @Override
    public Optional<TopUp> newTopup(NewTopUpForm params) {
        TopUp topUp = new TopUp();
        topUp.setName(params.getName());
        topUp.setPercentage(params.getPercentage());
        topUp.setTopupType(params.getTopupType());
        topUp.setFunderId(params.getFunderId());
        topUp.setProgramId(params.getProgramId());
        topUp.setActive(params.isActive());
        topUp.setFixedAmount(params.getFixedAmount());
        topUp.setHouseholdStatus(params.getHouseholdStatus());
        topUp.setDistrictCode(params.getDistrictCode());
//        topUp.setLocationType(params.getLocationType());
        topUp.setLocationType(LocationType.SUBNATIONAL1); // TODO:
        // new topups will by default not have been executed
        topUp.setExecuted(false);
        topUp.setAmountExecuted(null);
        topUp.setAmountProjected(null);

        if (params.isCategorical()) {
            topUp.setCategorical(params.isCategorical());
            topUp.setCategoricalTargetingCriteriaId(params.getCategoricalTargetingCriteriaId());
            // TODO: handle categorical parameters - create criteria record and link to the topup
            // TODO: handle params.getOrphanhoodStatuses() et al
        }
        topUp.setDiscountedFromFunds(params.isDiscountedFromFunds());


        LocalDateTime now = LocalDateTime.now();
        topUp.setCreatedBy(params.getUserId());
        topUp.setUpdatedBy(params.getUserId());
        topUp.setCreatedAt(now);
        topUp.setUpdatedAt(now);

        return Optional.of(topUpRepository.save(topUp));
    }

    @Override
    public List<TopUp> fetchAllActive() {
        return null;
    }

    @Override
    public List<TopUp> fetchAllActive(Location location) {
        return null;
    }

    @Override
    public List<TopUp> fetchAllExecuted() {
        return null;
    }

    @Override
    public List<TopUp> fetchAllExecuted(Location location) {
        return null;
    }

    @Override
    public void markAsExecuted(TopUp topUp, Long amount) {
        throw  new RuntimeException("not implemented");
    }

    @Override
    public List<TopUp> findAllActive() {
        return topUpRepository.findAllByIsActive(true);
    }

    @Override
    public Optional<TopUpView> findById(Long topupId) {
        return topUpRepository.findOneTopupById(topupId);
    }
}
