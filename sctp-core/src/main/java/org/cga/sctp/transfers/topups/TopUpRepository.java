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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopUpRepository extends JpaRepository<TopUp, Long> {
    @Query
    List<TopUp> findAllByIsActive(boolean value);

    @Query(nativeQuery = true, value = """
            SELECT 
            p.name AS programName,
            f.name AS funderName,
            l.name AS districtName,
            concat(cr8or.first_name, ' ', cr8or.last_name) AS createdByUser,
            concat(upd8r.first_name, ' ', upd8r.last_name) AS updatedByUser,
            top.id AS id,
            top.name AS name,
            top.funder_id AS funderId,
            top.program_id AS programId,
            top.district_code AS districtCode,
            top.cluster_codes AS clusterCodes,
            top.ta_codes AS taCodes,
            top.location_type AS locationType,
            top.is_discounted_from_funds AS isDiscountedFromFunds,
            top.is_categorical AS isCategorical,
            top.is_active AS isActive,
            top.is_executed AS isExecuted,
            top.topup_type AS topupType,
            top.household_status AS householdStatus,
            top.percentage AS percentage,
            top.categorical_targeting_criteria_id AS categoricalTargetingCriteriaId,
            top.fixed_amount AS fixedAmount,
            top.amount_projected AS amountProjected,
            top.amount_executed AS amountExecuted,
            top.created_by AS createdBy,
            top.updated_by AS updatedBy,
            top.created_at AS createdAt,
            top.updated_at AS updatedAt
            FROM transfer_topups as top
            INNER JOIN programs p ON p.id = top.program_id 
            INNER JOIN funders f ON f.id = top.funder_id 
            LEFT OUTER JOIN locations l ON l.code = top.district_code 
            INNER JOIN users cr8or ON cr8or.id = top.created_by 
            INNER JOIN users upd8r ON upd8r.id = top.updated_by""")
    List<TopUpView> fetchAllTopups();

    @Query(nativeQuery = true, value = """
            SELECT 
            p.name AS programName,
            f.name AS funderName,
            l.name AS districtName,
            concat(cr8or.first_name, ' ', cr8or.last_name) AS createdByUser,
            concat(upd8r.first_name, ' ', upd8r.last_name) AS updatedByUser,
            top.id AS id,
            top.name AS name,
            top.funder_id AS funderId,
            top.program_id AS programId,
            top.district_code AS districtCode,
            top.cluster_codes AS clusterCodes,
            top.ta_codes AS taCodes,
            top.location_type AS locationType,
            top.is_discounted_from_funds AS isDiscountedFromFunds,
            top.is_categorical AS isCategorical,
            top.is_active AS isActive,
            top.is_executed AS isExecuted,
            top.topup_type AS topupType,
            top.household_status AS householdStatus,
            top.percentage AS percentage,
            top.categorical_targeting_criteria_id AS categoricalTargetingCriteriaId,
            top.fixed_amount AS fixedAmount,
            top.amount_projected AS amountProjected,
            top.amount_executed AS amountExecuted,
            top.created_by AS createdBy,
            top.updated_by AS updatedBy,
            top.created_at AS createdAt,
            top.updated_at AS updatedAt
            FROM transfer_topups as top
            INNER JOIN programs p ON p.id = top.program_id 
            INNER JOIN funders f ON f.id = top.funder_id 
            LEFT OUTER JOIN locations l ON l.code = top.district_code 
            INNER JOIN users cr8or ON cr8or.id = top.created_by 
            INNER JOIN users upd8r ON upd8r.id = top.updated_by
            WHERE top.id = :topupId
            """)
    Optional<TopUpView> findOneTopupById(@Param("topupId") Long topupId);
}
