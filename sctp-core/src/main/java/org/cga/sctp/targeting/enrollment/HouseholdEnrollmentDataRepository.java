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

package org.cga.sctp.targeting.enrollment;

import org.cga.sctp.targeting.ExportCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface HouseholdEnrollmentDataRepository extends JpaRepository<HouseholdEnrollmentData, Long> {
    Page<HouseholdEnrollmentData> findBySessionId(Long sessionId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT
             hed.cluster_name name,
             hed.cluster_code code,
             count(DISTINCT household_id) households
             FROM household_enrollment_data hed
             WHERE session_id = :id
             GROUP BY cluster_code
             HAVING (count(DISTINCT household_id) > 0)
            """)
    List<ExportCluster> getExportClusters(@Param("id") Long id);

    @Query(nativeQuery = true, value = """
            select *
             from household_enrollment_data
             where session_id = :sid and cluster_code = :cc and status = :st
             LIMIT :l, :s
            """)
    List<HouseholdEnrollmentData> getForExportByStatus(
            @Param("sid") Long sessionId,
            @Param("cc") long clusterCode,
            @Param("st") String status,
            @Param("l") int page,
            @Param("s") int pageSize
    );

    @Query(nativeQuery = true, value = """
            select *
             from household_enrollment_data
             where session_id = :sid and cluster_code = :cc
             LIMIT :l, :s
            """)
    List<HouseholdEnrollmentData> getForExport(
            @Param("sid") Long sessionId,
            @Param("cc") long clusterCode,
            @Param("l") int page,
            @Param("s") int pageSize
    );
}
