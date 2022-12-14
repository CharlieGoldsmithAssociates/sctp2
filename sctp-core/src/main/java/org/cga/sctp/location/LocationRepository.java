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
    @Query("select (count(l) > 0) from Location l where l.code = ?1 and l.locationType = ?2")
    boolean existsByCodeAndType(long code, LocationType locationType);


    @Query(value = "SELECT * FROM locations_info_v WHERE active = :active ORDER BY id", nativeQuery = true)
    Page<LocationInfo> getByStatus(@Param("page") Pageable pageable, @Param("active") boolean active);

    @Query(value = "select * FROM locations_info_v where active = true ORDER BY name, code", nativeQuery = true)
    List<LocationInfo> findAllActive();

    @Query(nativeQuery = true, value = "select * from locations where active = true and id = :id")
    Location findActiveLocationById(@Param("id") Long locationId);

    @Query(nativeQuery = true, value = "select * from locations where active = true and location_type = :type ORDER BY name, code")
    List<Location> findActiveLocationsByType(@Param("type") String type);

    @Query(nativeQuery = true, value = "select * from locations_info_v where locationType = :type ORDER BY name, code")
    List<LocationInfo> findByLocationType(@Param("type") String type);

    @Query(nativeQuery = true, value = "select * from locations_info_v where locationType = :type limit 1")
    LocationInfo findByLocationTypeFirst(@Param("type") String type);

    @Query(nativeQuery = true, value = "select * from locations_info_v where parentId = :parent ORDER BY name, code")
    List<LocationInfo> getByParentId(@Param("parent") Long parentId);

    List<Location> getByActiveAndParentId(boolean active, Long parentId);

    Location getByActiveAndIdAndLocationType(boolean active, Long id, LocationType type);

    Location findByCode(long code);

    Boolean existsByCode(long code);

    @Query(nativeQuery = true, value = "select id, name, code, parentCode, location_type locationType from location_by_codes_v where parentCode = :code  ORDER BY name, code")
    List<LocationCode> getCodesByParentCode(@Param("code") Long code);

    @Query(nativeQuery = true, value = "select id, name, code, parentCode, location_type locationType from location_by_codes_v where parentCode in :codes  ORDER BY name, code")
    List<LocationCode> getCodesByParentCodeIn(@Param("codes") List<Long> codes);

    @Query(nativeQuery = true, value = "select id, name, code, parentCode, location_type locationType from location_by_codes_v where code in :codes  ORDER BY name, code")
    List<LocationCode> findAllByCodeIn(@Param("codes") List<Long> codes);

    @Query(nativeQuery = true, value = "select id, name, code, parentCode, location_type locationType from location_by_codes_v where location_type = :type AND active = true ORDER BY name, code")
    List<LocationCode> getActiveCodesByType(@Param("type") String type);

    @Query(value = "select * from active_locations where code = :code and location_type = :type", nativeQuery = true)
    Location findByActiveAndCodeAndLocationType(@Param("code") long code, @Param("type") String type);

    @Query(nativeQuery = true, value = "SELECT COUNT(l.id) FROM locations l INNER JOIN transfer_agencies_assignments taa ON taa.location_id = l.id  WHERE l.id = :id AND l.active = 1;")
    Integer countNumberOfTransferAgenciesAssigned(@Param("id") Long locationId);


    @Query(nativeQuery = true, value = "select district_code code, district_name name, household_count householdCount from household_districts_view")
    List<HouseholdLocation> getHouseholdDistricts();

    @Query(nativeQuery = true, value = "select ta_code code, ta_name name, household_count householdCount from household_traditional_authorities_view")
    List<HouseholdLocation> getHouseholdTAs();

    @Query(nativeQuery = true, value = "select ta_code code, ta_name name, household_count householdCount from household_traditional_authorities_view where district_code = :code")
    List<HouseholdLocation> getHouseholdTAsByDistrictCode(@Param("code") long parentCode);

    @Query(nativeQuery = true, value = "select cluster_code code, cluster_name name, household_count householdCount from household_clusters_view")
    List<HouseholdLocation> getHouseholdClusters();

    @Query(nativeQuery = true, value = "select cluster_code code, cluster_name name, household_count householdCount from household_clusters_view where ta_code = :code")
    List<HouseholdLocation> getHouseholdClustersByTaCode(@Param("code") long parentCode);

    default List<HouseholdLocation> getHouseholdLocations(LocationType locationType, Long parentCode) {
        return
                switch (locationType) {
                    case SUBNATIONAL1 -> getHouseholdDistricts();
                    case SUBNATIONAL2 ->
                            parentCode != null ? getHouseholdTAsByDistrictCode(parentCode) : getHouseholdTAs();
                    case SUBNATIONAL3 ->
                            parentCode != null ? getHouseholdClustersByTaCode(parentCode) : getHouseholdClusters();
                    default ->
                            throw new UnsupportedOperationException("Location type " + locationType + " currently not supported");
                };
    }
}
