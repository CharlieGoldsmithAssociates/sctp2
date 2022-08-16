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
    // @Query(nativeQuery = true, value = "CALL initiateTransfersForEnrolledHouseholdsInDistrict(:enrollmentSessionId, :transferSessionId, :userId)")
    @Modifying
    @Query(nativeQuery = true, value ="""   
          INSERT INTO transfers (
            transfer_session_id,
            transfer_period_id,
            program_id,
            household_id,
            receiver_id,
            transfer_state,
            transfer_agency_id,
            district_id,
            village_cluster_id,
            traditional_authority_id,
            zone_id,
            household_member_count,
            basic_subsidy_amount,
            number_of_months,
            children_count,
            primary_children_count,
            primary_incentive_amount,
            secondary_children_count,
            secondary_incentive_amount,
            is_first_transfer,
            total_transfer_amount,
            is_suspended,
            is_withheld,
            account_number,
            amount_disbursed,
            is_collected,
            disbursement_date,
            arrears_amount,
            disbursed_by_user_id,
            is_reconciled,
            reconciliation_method,
            topup_event_id,
            topup_amount,
            created_by,
            reviewed_by,
            created_at,
            modified_at
          )
          SELECT
            :transferPeriodId,
            ts.id as transfer_session_id,
            ts.program_id AS program_id,
            h.household_id,
            null as receiver_id,
            '19' as transfer_state, -- Pre-Close
            null as transfer_agency_id,
            l.id as district_id,
            l4.id as village_cluster_id,
            l2.id as traditional_authority_id,
            l3.id AS  zone_id,
            (SELECT count(id) FROM individuals i2 WHERE i2.household_id = h.household_id) AS household_member_count,
            0 as basic_subsidy_amount,
            0 as number_of_months,
            (select count(id) from individuals i4 WHERE i4.household_id = h.household_id and TIMESTAMPDIFF(YEAR, i4.date_of_birth, CURDATE()) >=6 and TIMESTAMPDIFF(YEAR, i4.date_of_birth, CURDATE()) <= 15 ) as children_count,
            (select count(id) from individuals i5 WHERE i5.household_id = h.household_id AND i5.highest_education_level = 2) as primary_children_count,
            0 as primary_incentive_amount,
            (select count(id) from individuals i6 WHERE i6.household_id = h.household_id AND i6.highest_education_level = 3) as secondary_children_count,
            0 as secondary_incentive_amount,
            0 AS is_first_transfer,
            0 AS total_transfer_amount,
            0 AS is_suspended,
            0 AS is_withheld,
            NULL AS account_number,
            0 AS amount_disbursed,
            0 AS is_collected,
            NULL AS disbursement_date,
            0 AS arrears_amount,
            0 AS disbursed_by_user_id,
            0 AS is_reconciled,
            NULL AS reconciliation_method,
            0 AS topup_event_id,
            0 AS topup_amount,
            :userId AS created_by, 
            0 AS reviewed_by,
            NOW() AS created_at,
            NOW() AS modified_at
        from household_enrollment he
        LEFT join households h on h.household_id = he.household_id
        LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1 
        LEFT JOIN locations l ON l.code = h.location_code
        LEFT JOIN locations l2 ON l2.code = h.ta_code
        LEFT JOIN locations l3 ON l3.code = h.zone_code
        LEFT JOIN locations l4 ON l4.code = h.cluster_code
        LEFT JOIN locations l5 ON l5.code = h.village_code
        LEFT JOIN enrollment_sessions es ON es.id = :enrollmentSessionId 
        LEFT JOIN transfers_sessions ts ON ts.id = :transferSessionId
        WHERE l.id = :districtId AND he.status = 5 ;-- CbtStatus '5' is Enrolled
        """)
    void initiateTransfersForEnrolledHouseholds(@Param("enrollmentSessionId") Long enrollmentSessionId,
                                                @Param("transferSessionId") Long transferSessionId,
                                                @Param("transferPeriodId") Long transferPeriodId,
                                                @Param("districtId") Long districtId,
                                                @Param("userId") Long userId);

}
