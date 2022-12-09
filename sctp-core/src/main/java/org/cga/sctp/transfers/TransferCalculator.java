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

import org.cga.sctp.location.Location;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.transfers.parameters.EducationTransferParameter;
import org.cga.sctp.transfers.parameters.HouseholdParameterCondition;
import org.cga.sctp.transfers.parameters.HouseholdTransferParameter;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.cga.sctp.transfers.topups.TopUp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TransferCalculator {
    private List<EducationTransferParameter> educationTransferParameters;
    private List<HouseholdTransferParameter> householdTransferParameters;

    private List<TopUp> topUps;

    /**
     * @param householdTransferParameters Parameters
     * @param educationTransferParameters Parameters for education
     * @param topUps                      topups that are applicable to the households to calculate transfers for
     */
    public TransferCalculator(List<HouseholdTransferParameter> householdTransferParameters,
                              List<EducationTransferParameter> educationTransferParameters,
                              List<TopUp> topUps) {
        this.educationTransferParameters = educationTransferParameters;
        this.householdTransferParameters = householdTransferParameters;
        this.topUps = topUps;
    }


    public BigDecimal getSecondaryBonusAmount() {
        // TODO: don't fetch from the db on every call/invocation of this method
        return getSecondaryBonusAmount(educationTransferParameters);
    }

    public BigDecimal getSecondaryBonusAmount(List<EducationTransferParameter> parameters) {
        Optional<EducationTransferParameter> educationTransferParameterOptional = parameters.stream()
                .filter(e -> e.getEducationLevel().equals(EducationLevel.Secondary))
                .findFirst();

        if (educationTransferParameterOptional.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return educationTransferParameterOptional.get().getAmount();
    }

    public BigDecimal getPrimaryBonusAmount() {
        // TODO: don't fetch from the db on every call/invocation of this method
        return this.getPrimaryBonusAmount(educationTransferParameters);
    }

    public BigDecimal getPrimaryBonusAmount(List<EducationTransferParameter> educationTransferParameters) {
        Optional<EducationTransferParameter> educationTransferParameterOptional = educationTransferParameters
                .stream()
                .filter(e -> e.getEducationLevel().equals(EducationLevel.Primary))
                .findFirst();

        if (educationTransferParameterOptional.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return educationTransferParameterOptional.get().getAmount();
    }

    public BigDecimal getPrimaryIncentiveAmount() {
        // NOTE: At the moment the primary bonus amount is the same as the incentive.
        return getPrimaryBonusAmount();
    }

    public BigDecimal getPrimaryIncentiveAmount(List<EducationTransferParameter> educationTransferParameters) {
        return getPrimaryBonusAmount(educationTransferParameters);
    }

    /**
     * Get the basic amount to be disbursed to the household according to it's size
     *
     * @param householdSize   size of the household
     * @param householdParams household params
     * @return amount
     */
    public BigDecimal determineAmountByHouseholdSize(int householdSize, List<HouseholdTransferParameter> householdParams) {
        for (var param : householdParams) {
            if (param.getCondition().equals(HouseholdParameterCondition.GREATER_THAN) &&
                    householdSize > param.getNumberOfMembers()) {

                return param.getAmount();

            } else if (param.getCondition().equals(HouseholdParameterCondition.GREATER_THAN_OR_EQUALS) &&
                    householdSize >= param.getNumberOfMembers()) {

                return param.getAmount();
            } else if (param.getCondition().equals(HouseholdParameterCondition.EQUALS) &&
                    householdSize == param.getNumberOfMembers()) {

                return param.getAmount();
            }
        }

        return BigDecimal.ZERO;
    }

    protected BigDecimal calculateTopUpAmount(Transfer transfer,  List<TopUp> topUpList) {
        BigDecimal monthlyAmount = transfer.calculateMonthlyAmount();
        if (monthlyAmount == null) {
            return null; // TODO: should we set the topup to be 0.0 ?
        }
        BigDecimal totalTopupAmout = new BigDecimal("0.0");
        BigDecimal curTopUp = null;
        for (var topup : topUpList) {
            curTopUp = switch (topup.getTopupType()) {
                case FIXED_AMOUNT -> topup.getFixedAmount();
                case EQUIVALENT_BENEFICIARY_AMOUNT -> monthlyAmount;
                case PERCENTAGE_OF_RECIPIENT_AMOUNT ->
                        monthlyAmount.multiply(topup.getPercentage()).divide(new BigDecimal("100.00"));
                default -> topup.getFixedAmount();
            };
            totalTopupAmout = totalTopupAmout.add(curTopUp);
        }

        return totalTopupAmout;
    }

    public void calculateTransferAmount(Transfer transfer, TransferPeriod transferPeriod) {
        final BigDecimal basicAmount = determineAmountByHouseholdSize(transfer.getHouseholdMemberCount(), householdTransferParameters);
        final BigDecimal secondaryBonus = getSecondaryBonusAmount(educationTransferParameters).multiply(BigDecimal.valueOf(transfer.getSecondaryChildrenCount()));
        final BigDecimal primaryBonus = getPrimaryBonusAmount(educationTransferParameters).multiply(BigDecimal.valueOf(transfer.getPrimaryChildrenCount()));
        final BigDecimal primaryIncentive = getPrimaryIncentiveAmount().multiply(BigDecimal.valueOf(transfer.getPrimaryIncentiveChildrenCount()));

        transfer.setAmountDisbursed(null);
        transfer.setNumberOfMonths(transferPeriod.countNoOfMonths());
        transfer.setBasicSubsidyAmount(basicAmount);
        transfer.setPrimaryIncentiveAmount(primaryIncentive);
        transfer.setPrimaryBonusAmount(primaryBonus);
        transfer.setSecondaryBonusAmount(secondaryBonus);
        // Calculate topups
        transfer.setTopupAmount(calculateTopUpAmount(transfer, topUps));
    }

    /**
     * Calculates transfers for all the households in the transfer period for a given location..
     *
     * @param transferPeriod
     */
    public void calculateTransfers(TransferPeriod transferPeriod, List<Transfer> transferList) {
        for (var transfer : transferList) {
            if (transfer.getTransferPeriodId() != transferPeriod.getId()) {
                continue;
            }
            if (transfer != null)
                this.calculateTransferAmount(transfer, transferPeriod);
        }
    }
}
