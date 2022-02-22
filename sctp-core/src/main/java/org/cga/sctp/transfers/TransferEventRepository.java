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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferEventRepository extends JpaRepository<TransferEvent, Long> {
    // TODO(zikani03): make a view with the actual data...
    List<TransferEvent> findTransfersByTransferSessionId(Long id);

    @Query(nativeQuery = true, value = """
            SELECT
                h.household_id householdId
                ,h.ubr_code formNumber
                ,l.name districtName
                ,l2.name taName
                ,l3.name zoneName
                ,l4.name clusterName
                ,l5.name villageName
                ,h.ml_code mlCode
                ,h.group_village_head_name villageHeadName
                , CONCAT(i.first_name, ' ', i.last_name) householdHead
                ,(SELECT count(id) FROM individuals i2 WHERE i2.household_id = h.household_id) AS memberCount
                ,(select count(id) from individuals i3 where i3.household_id = h.household_id and TIMESTAMPDIFF(YEAR, i3.date_of_birth, CURDATE()) >=6 and TIMESTAMPDIFF(YEAR, i3.date_of_birth, CURDATE()) <= 15 ) as totalChildren
                ,(select count(id) from individuals i3 where i3.household_id = h.household_id and i3.highest_education_level = 2) as primaryChildren
                ,(select count(id) from individuals i3 where i3.household_id = h.household_id and i3.highest_education_level = 3) as secondaryChildren
                , '' as receiverName
                , 0 as primaryIncentive
                , 0 as secondaryIncentive
                , 0 as monthlyAmount
                , 0 as numberOfMonths
                , 0 as totalMonthlyAmount
                , 0 as totalArrears
                , 0 as totalAmount
                , 0 as isFirstTransfer
            FROM households h
            INNER JOIN transfers_events te ON h.household_id = te.household_id
            LEFT JOIN locations l ON l.code = h.location_code
            LEFT JOIN locations l2 ON l2.code = h.ta_code
            LEFT JOIN locations l3 ON l3.code = h.zone_code
            LEFT JOIN locations l4 ON l4.code = h.cluster_code
            LEFT JOIN locations l5 ON l5.code = h.village_code
            LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1 
            WHERE te.transfer_session_id = :sessionId
            """) // TODO(zikani03): fix the values in the query from receiverName going on downwards
    List<TransferEventHouseholdView> findAllHouseholdsByTransferSessionId(@Param("sessionId") Long id);
}
