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

package org.cga.sctp.schools.educationzone;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationZoneRepository extends JpaRepository<EducationZone, Long> {


    @Query(nativeQuery = true, value = """
                SELECT 
                    eduzone.id AS id,
                    ta.name AS taName ,
                    district.name as districtName,
                    eduzone.ta_code AS taCode,
                    eduzone.district_code AS districtCode,
                    eduzone.name,
                    eduzone.alt_name AS altName,
                    eduzone.code,
                    eduzone.active,
                    eduzone.created_at AS createdAt,
                    eduzone.updated_at AS updatedAt
                FROM education_zones eduzone
                LEFT JOIN locations district ON district.code = eduzone.district_code
                LEFT JOIN locations ta ON ta.code = eduzone.ta_code
            """)
    Page<EducationZoneView> fetchAllZonesPaged(Pageable pageable);

    @Query(nativeQuery = true, value = """
                SELECT 
                    eduzone.id AS id,
                    ta.name AS taName ,
                    district.name as districtName,
                    eduzone.ta_code AS taCode,
                    eduzone.district_code AS districtCode,
                    eduzone.name,
                    eduzone.alt_name AS altName,
                    eduzone.code,
                    eduzone.active,
                    eduzone.created_at AS createdAt,
                    eduzone.updated_at AS updatedAt
                FROM education_zones eduzone
                LEFT JOIN locations district ON district.code = eduzone.district_code
                LEFT JOIN locations ta ON ta.code = eduzone.ta_code
                WHERE eduzone.id = :educationZoneId
            """)
    Optional<EducationZoneView> findByIdAsView(@Param("educationZoneId") Long educationZoneId);
}
