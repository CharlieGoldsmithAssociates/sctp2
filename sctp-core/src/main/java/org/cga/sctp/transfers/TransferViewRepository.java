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

public interface TransferViewRepository extends JpaRepository<TransferView, Long> {
    // Find All By Period By Location To District Level
    @Query(nativeQuery = true, value = """
            SELECT t.*
             FROM transfers_v t
             INNER JOIN households h ON h.household_id = t.household_id
             LEFT JOIN locations l ON l.code = h.location_code
             WHERE ( t.transfer_period_id = :periodId
                   AND l.code = :districtCode
                   )
                 LIMIT :pageNumber, :pageSize ;
             """)
    List<TransferView> findAllByPeriodByLocationToDistrictLevel(
            @Param("periodId") long periodId,
            @Param("districtCode") long districtCode,// 213 for dedza
            @Param("pageNumber") int pageNumber,
            @Param("pageSize") int pageSize);

    // Find All By Period By Location To TA Level
    @Query(nativeQuery = true, value = """
            SELECT t.*
             FROM transfers_v t
             INNER JOIN households h ON h.household_id = t.household_id
             LEFT JOIN locations l ON l.code = h.location_code
             LEFT JOIN locations l2 ON l2.code = h.ta_code
             WHERE ( t.transfer_period_id = :periodId
                   AND l.code = :districtCode
                   AND l2.code = :taCode
                   )
                 LIMIT :pageNumber, :pageSize ;
             """)
    List<TransferView> findAllByPeriodByLocationToTALevel(
            @Param("periodId") long periodId,
            @Param("districtCode") long districtCode,// 213 for dedza
            @Param("taCode") Long taCode,   // 21301 for Chauma
            @Param("pageNumber") int pageNumber,
            @Param("pageSize") int pageSize);

    // Find All By Period By Location To Cluster Level
    @Query(nativeQuery = true, value = """
            SELECT t.*
             FROM transfers_v t
             INNER JOIN households h ON h.household_id = t.household_id
             LEFT JOIN locations l ON l.code = h.location_code
             LEFT JOIN locations l2 ON l2.code = h.ta_code
             LEFT JOIN locations l3 ON l3.code = h.cluster_code
             WHERE ( t.transfer_period_id = :periodId
                   AND l.code = :districtCode
                   AND l2.code = :taCode
                   AND l3.code = :clusterCode
                   )
                 LIMIT :pageNumber, :pageSize ;
             """)
    List<TransferView> findAllByPeriodByLocationToClusterLevel(
            @Param("periodId") long periodId,
            @Param("districtCode") long districtCode,// 213 for dedza
            @Param("taCode") Long taCode,   // 21301 for Chauma
            @Param("clusterCode") Long clusterCode,  // 21301001
            @Param("pageNumber") int pageNumber,
            @Param("pageSize") int pageSize);

    // Find All By Period By Location To Zone Level
    @Query(nativeQuery = true, value = """
            SELECT t.*
             FROM transfers_v t
             INNER JOIN households h ON h.household_id = t.household_id
             LEFT JOIN locations l ON l.code = h.location_code
             LEFT JOIN locations l2 ON l2.code = h.ta_code
             LEFT JOIN locations l3 ON l3.code = h.cluster_code
             LEFT JOIN locations l4 ON l4.code = h.zone_code
             WHERE ( t.transfer_period_id = :periodId
                   AND l.code = :districtCode
                   AND l2.code = :taCode
                   AND l3.code = :clusterCode
                   AND l4.code = :zoneCode
                   )
                 LIMIT :pageNumber, :pageSize ;
             """)
    List<TransferView> findAllByPeriodByLocationToZoneLevel(
            @Param("periodId") long periodId,
            @Param("districtCode") long districtCode,
            @Param("taCode") Long taCode,
            @Param("clusterCode") Long clusterCode,
            @Param("zoneCode") Long zoneCode,
            @Param("pageNumber") int pageNumber,
            @Param("pageSize") int pageSize);

    // Find All By Period By Location To Village Level
    @Query(nativeQuery = true, value = """
            SELECT t.*
             FROM transfers_v t
             INNER JOIN households h ON h.household_id = t.household_id
             LEFT JOIN locations l ON l.code = h.location_code
             LEFT JOIN locations l2 ON l2.code = h.ta_code
             LEFT JOIN locations l3 ON l3.code = h.cluster_code
             LEFT JOIN locations l4 ON l4.code = h.zone_code
             LEFT JOIN locations l5 ON l5.code = h.village_code
             WHERE ( t.transfer_period_id = :periodId
                   AND l.code = :districtCode
                   AND l2.code = :taCode
                   AND l3.code = :clusterCode
                   AND l4.code = :zoneCode
                   AND l5.code = :villageCode )
                 LIMIT :pageNumber, :pageSize ;
             """)
    List<TransferView> findAllByPeriodByLocationToVillageLevel(
            @Param("periodId") long periodId,
            @Param("districtCode") long districtCode,
            @Param("taCode") Long taCode,
            @Param("clusterCode") Long clusterCode,
            @Param("zoneCode") Long zoneCode,
            @Param("villageCode") Long villageCode,
            @Param("pageNumber") int pageNumber,
            @Param("pageSize") int pageSize);
}
