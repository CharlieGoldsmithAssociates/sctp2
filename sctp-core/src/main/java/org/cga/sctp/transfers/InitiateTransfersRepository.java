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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Specialized Repository for initiating/creating transfer records for Households that have completed Enrollment.
 *
 * TODO(zikani03): Move this into a stored procedure
 * NOTE: a procedure already exists named initiateTransfersForEnrolledHouseholdsInDistrict / initiateTransfersInDistrict
 * but it became annoying to keep creating migration files to DROP/CREATE it because the design
 * of TransferPeriods and other things kept changing. So once the design is solidified, we can move this monster
 * to it's own stored procedure.
 */
interface InitiateTransfersRepository extends JpaRepository<Transfer, Long> {
    @Modifying
    @Query(nativeQuery = true, value ="""   
            INSERT INTO transfers (
                   transfer_period_id,
                   h.household_id,
                   receiver_id,
                   transfer_state,
                   transfer_agency_id,
                   household_member_count,
                   children_count,
                   primary_children_count,
                   secondary_children_count,
                   account_number,
                   disbursement_date,
                   reconciliation_method,
                   basic_subsidy_amount,
                   number_of_months,
                   primary_incentive_amount,
                   secondary_incentive_amount,
                   is_first_transfer,
                   total_transfer_amount,
                   is_suspended,
                   is_withheld,
                   amount_disbursed,
                   is_collected,
                   arrears_amount,
                   disbursed_by_user_id,
                   is_reconciled,
                   topup_event_id,
                   topup_amount,
                   created_by,
                   reviewed_by,
                   created_at,
                   modified_at
             )
            SELECT
               :transferPeriodId,
               h.household_id,
               he.alternate_recipient as receiver_id,
               '19' as transfer_state, -- Pre-Close
               (SELECT transfer_agency_id FROM transfer_agencies_assignments ta WHERE ta.location_id = he.district_code LIMIT 1) as transfer_agency_id,
               he.member_count as household_member_count,
               he.child_enrollment6to15 as children_count,
               he.primary_children as primary_children_count,
               he.secondary_children as secondary_children_count,
               NULL AS account_number,  \s
               NULL AS disbursement_date,
               NULL AS reconciliation_method,
               0 as basic_subsidy_amount,
               0 as number_of_months,
               0 as primary_incentive_amount,
               0 as secondary_incentive_amount,
               0 AS is_first_transfer,
               0 AS total_transfer_amount,
               0 AS is_suspended,
               0 AS is_withheld,
               0 AS amount_disbursed,
               0 AS is_collected,
               0 AS arrears_amount,
               0 AS disbursed_by_user_id,
               0 AS is_reconciled,
               0 AS topup_event_id,
               0 AS topup_amount,
               :userId AS created_by,
               0 AS reviewed_by,
               NOW() AS created_at,
               NOW() AS modified_at
            from household_enrollment_data he
            LEFT join households h on h.household_id = he.household_id
            LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
            LEFT JOIN locations l ON l.code = h.location_code
            LEFT JOIN enrollment_sessions es ON es.id = :enrollmentSessionId 
            WHERE l.id = :districtId AND he.status = 'Enrolled'
            """)
    void initiateTransfersForEnrolledHouseholds(@Param("enrollmentSessionId") Long enrollmentSessionId,
                                                @Param("transferPeriodId") Long transferPeriodId,
                                                @Param("districtId") Long districtId,
                                                @Param("userId") Long userId);

}
