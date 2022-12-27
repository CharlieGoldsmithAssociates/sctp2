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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cga.sctp.targeting.enrollment.HouseholdEnrollmentData;
import org.cga.sctp.targeting.enrollment.HouseholdRecipientJsonConverter;
import org.cga.sctp.targeting.importation.converters.GenderParameterValueConverter;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "transfers_v")
public class TransferView extends TransferBase {
    @Column
    private String householdMlCode;
    @Column
    private String formNumber;
    @Column
    private String transferAgencyName;

    /**
     * BIGINT COMMENT 'monthlyAmount * number_of_months + topup_amount'
     */
    @Column
    private BigDecimal totalAmountToTransfer;

    /**
     * BIGINT COMMENT 'A sum of basic_subsidy_amount, secondary_bonus_amount, primary_bonus_amount, primary_incentive_amount'
     */
    @Column
    private BigDecimal monthlyAmount;

    /**
     * COMMENT 'Household head name'
     */
    @Column
    private String headName;

    /**
     * COMMENT 'Household head gender. 1:Male, 2:Female'
     */
    @Column
    @Convert(converter = GenderParameterValueConverter.class)
    private Gender headGender;

    /**
     * COMMENT 'Household head memberCode'
     */
    @Column
    private String headMemberCode;

    @Column
    @JsonProperty("main_recipient")
    @Convert(converter = HouseholdRecipientJsonConverter.class)
    private HouseholdEnrollmentData.HouseholdRecipientInfo mainRecipient;

    @Column
    @JsonProperty("secondary_recipient")
    @Convert(converter = HouseholdRecipientJsonConverter.class)
    private HouseholdEnrollmentData.HouseholdRecipientInfo secondaryRecipient;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "ta_name", nullable = false, length = 100)
    private String taName;

    @Column(name = "gvh_name", nullable = false, length = 100)
    private String gvhName;

    @Column(name = "cluster_name", nullable = false, length = 100)
    private String clusterName;

    @Column(name = "village_name", nullable = false, length = 100)
    private String villageName;

    private String zoneName;

    public String getHouseholdMlCode() {
        return householdMlCode;
    }

    public String getFormNumber() {
        return formNumber;
    }

    public String getTransferAgencyName() {
        return transferAgencyName;
    }

    public BigDecimal getTotalAmountToTransfer() {
        return totalAmountToTransfer;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    public String getHeadName() {
        return headName;
    }

    public Gender getHeadGender() {
        return headGender;
    }

    public String getHeadMemberCode() {
        return headMemberCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getTaName() {
        return taName;
    }

    public String getGvhName() {
        return gvhName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getVillageName() {
        return villageName;
    }

    public String getZoneName() {
        return zoneName;
    }
}
