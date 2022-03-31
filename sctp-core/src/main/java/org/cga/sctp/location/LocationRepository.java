/*
 * Copyright (C) 2021 CGA Technologies, a trading name of Charlie Goldsmith Associates Ltd
 *  All rights reserved, released under the BSD-3 licence.
 *
 * CGA Technologies develop and use this software as part of its work
 *  but the software itself is open-source software; you can redistribute it and/or modify
 *  it under the terms of the BSD licence below
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS
 *  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 * For more information please see http://opensource.org/licenses/BSD-3-Clause
 */

package org.cga.sctp.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface LocationRepository extends JpaRepository<Location, Long> {


    @Query(value = "SELECT * FROM locations_info_v WHERE active = :active", nativeQuery = true)
    Page<LocationInfo> getByStatus(@Param("page") Pageable pageable, @Param("active") boolean active);

    @Query(value = "select * FROM locations_info_v where active = true", nativeQuery = true)
    List<LocationInfo> findAllActive();

    @Query(nativeQuery = true, value = "select * from locations where active = true and id = :id")
    Location findActiveLocationById(@Param("id") Long locationId);

    @Query(nativeQuery = true, value = "select * from locations where active = true and location_type = :type")
    List<Location> findActiveLocationsByType(@Param("type") String type);

    @Query(nativeQuery = true, value = "select * from locations_info_v where locationType = :type")
    List<LocationInfo> findByLocationType(@Param("type") String type);

    @Query(nativeQuery = true, value = "select * from locations_info_v where locationType = :type limit 1")
    LocationInfo findByLocationTypeFirst(@Param("type") String type);

    @Query(nativeQuery = true, value = "select * from locations_info_v where parentId = :parent")
    List<LocationInfo> getByParentId(@Param("parent") Long parentId);

    List<Location> getByActiveAndParentId(boolean active, Long parentId);

    Location getByActiveAndIdAndLocationType(boolean active, Long id, LocationType type);

    Location findByCode(long code);

    @Query(nativeQuery = true, value = "select id, name, code, location_type from location_by_codes_v where parentCode = :code")
    List<LocationCode> getCodesByParentCode(@Param("code") Long code);

    @Query(nativeQuery = true, value = "select id, name, code, location_type from location_by_codes_v where location_type = :type")
    List<LocationCode> getActiveCodesByType(@Param("type") String type);

    Location findByActiveAndCodeAndLocationType(boolean active, Long code, LocationType type);

    @Query(nativeQuery = true, value = "SELECT COUNT(l.id) FROM locations l INNER JOIN transfer_agencies_assignments taa ON taa.location_id = l.id  WHERE l.id = :id AND l.active = 1;")
    Integer countNumberOfTransferAgenciesAssigned(@Param("id") Long locationId);
}
