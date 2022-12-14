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

import org.cga.sctp.application.SystemInformation;
import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.importation.location.GeoLocationImport;
import org.cga.sctp.targeting.importation.location.ImportLocationType;
import org.cga.sctp.targeting.importation.location.task.LocationImportSessionCleaner;
import org.cga.sctp.targeting.importation.location.task.UbrGeoLocationImportTask;
import org.cga.sctp.targeting.importation.ubrapi.UbrApiConfiguration;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class LocationService extends TransactionalService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationImportSessionRepository importSessionRepository;

    @Autowired
    private LocationStatusRepository locationStatusRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UbrApiConfiguration apiConfiguration;

    @Autowired
    private SystemInformation systemInformation;

    @Autowired
    private JobScheduler jobScheduler;

    private LocationImportSessionCleaner importSessionCleaner;
    private UbrGeoLocationImportTask ubrGeoLocationImportTask;

    @Bean
    public LocationImportSessionCleaner importSessionCleaner() {
        return importSessionCleaner = new LocationImportSessionCleaner(this);
    }

    @Bean
    public UbrGeoLocationImportTask ubrGeoLocationImportTask() {
        return ubrGeoLocationImportTask = new UbrGeoLocationImportTask(
                this,
                systemInformation,
                apiConfiguration
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    void onApplicationStartup() {
        jobScheduler.enqueue(importSessionCleaner::doWork);
    }

    public Page<Location> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    public boolean isValidLocationHieArchyLevel(LocationType parent, LocationType child) {
        return parent.level < child.level;
    }

    public boolean isValidLocationCode(Long code) {
        return locationRepository.existsByCode(code);
    }

    public void addLocation(Location geoLocation) {
        locationRepository.save(geoLocation);
    }

    public Location findById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }

    public Page<LocationInfo> getLocationsByActiveStatus(Pageable pageable, boolean active) {
        return locationRepository.getByStatus(pageable, active);
    }

    public List<Location> getActiveDistricts() {
        return locationRepository.findActiveLocationsByType(LocationType.SUBNATIONAL1.name());
    }

    public Location findActiveLocationById(Long locationId) {
        return locationRepository.findActiveLocationById(locationId);
    }

    public void save(Location location) {
        locationRepository.save(location);
    }

    public List<LocationInfo> getByType(LocationType type) {
        return locationRepository.findByLocationType(type.name());
    }

    public LocationInfo getByTypeFirst(LocationType type) {
        return locationRepository.findByLocationTypeFirst(type.name());
    }

    public List<LocationInfo> getCountries() {
        return getByType(LocationType.COUNTRY);
    }

    public List<Location> getActiveCountries() {
        return getActiveByType(LocationType.COUNTRY);
    }

    public List<LocationInfo> getByParent(Location location) {
        return locationRepository.getByParentId(location.getId());
    }

    public List<Location> getActiveByType(LocationType type) {
        return locationRepository.findActiveLocationsByType(type.name());
    }

    public List<Location> getActiveByParentId(Long parentId) {
        return locationRepository.getByActiveAndParentId(true, parentId);
    }

    public Location findActiveLocationByIdAndType(Long id, LocationType type) {
        return locationRepository.getByActiveAndIdAndLocationType(true, id, type);
    }

    public Location findByCode(long code) {
        return locationRepository.findByCode(code);
    }

    public List<LocationCode> getLocationCodesByParent(Long parentCode) {
        return locationRepository.getCodesByParentCode(parentCode);
    }

    public List<LocationCode> getLocationCodesByParentIn(List<Long> parentCodes) {
        return locationRepository.getCodesByParentCodeIn(parentCodes);
    }

    public List<LocationCode> findAllByCodeIn(List<Long> codes) {
        return locationRepository.findAllByCodeIn(codes);
    }

    public List<LocationCode> getActiveDistrictCodes() {
        return locationRepository.getActiveCodesByType(LocationType.SUBNATIONAL1.name());
    }

    public List<LocationCode> getCodesByType(LocationType locationType) {
        return locationRepository.getActiveCodesByType(locationType.name());
    }

    public Location findActiveLocationByCodeAndType(Long code, LocationType type) {
        return locationRepository.findByActiveAndCodeAndLocationType(code, type.name());
    }

    public Page<LocationStatus> getLocationStatuses(Pageable pageable) {
        return locationStatusRepository.findAll(pageable);
    }

    /**
     * Checks whether the location has any Transfer Agencies assigned either directly or hierarchically, i.e.
     * there is a Transfer Agency assigned to a parent or grand-parent of the location.
     *
     * @param location location to check
     * @return whether transfer agency has been assigned to this location
     */
    public boolean locationHasTransferAgency(@NonNull final Location location) {
        return locationRepository.countNumberOfTransferAgenciesAssigned(location.getId()) > 0;
    }

    public boolean hasActiveLocationImportSession() {
        return importSessionRepository.getActiveSessionCount() >= 1;
    }

    @Transactional
    public void importLocations(List<GeoLocationImport> list, ImportLocationType locationType) {
        if (list.isEmpty()) {
            return;
        }
        ZonedDateTime timestamp = ZonedDateTime.now();
        LocationType misLocationType = locationType.asSubnation();

        for (GeoLocationImport location : list) {

            String sql = """
                    INSERT INTO locations (code, name, created_at, parent_id, location_type, active, tmp_parent_code)
                     VALUES (:code, :name, :created_at, :parent_id, :location_type, :active, :tmp_parent_code)
                     ON DUPLICATE KEY
                     UPDATE name = :name, location_type = :location_type, active = :active, tmp_parent_code = :tmp_parent_code
                    """;
            entityManager.createNativeQuery(sql)
                    .setParameter("code", location.getCode())
                    .setParameter("name", location.getName())
                    .setParameter("created_at", timestamp)
                    .setParameter("parent_id", null) // temporary
                    .setParameter("location_type", misLocationType.name())
                    .setParameter("active", location.isActive())
                    .setParameter("tmp_parent_code", location.getParentCode())
                    .executeUpdate();
        }

        // update the relationship
        String sql = """
                UPDATE locations c
                 JOIN locations p ON p.code = c.tmp_parent_code
                 SET c.parent_id = p.id
                 WHERE c.tmp_parent_code IS NOT NULL;
                """;
        entityManager.createNativeQuery(sql)
                .executeUpdate();

        // set districts parent to COUNTRY(1)
        sql = "UPDATE locations SET parent_id = 1, tmp_parent_code = 1 WHERE location_type = :location_type";
        entityManager.createNativeQuery(sql)
                .setParameter("location_type", LocationType.SUBNATIONAL1.name())
                .executeUpdate();
    }

    public LocationImportSessionSummary getLatestImportSession() {
        return importSessionRepository.getLatestSessionImportSummary();
    }

    public void saveLocationImportSession(LocationImportSession session) {
        importSessionRepository.save(session);
    }

    public void startLocationImport(LocationImportSession session) {
        saveLocationImportSession(session);
        BackgroundJob.enqueue(() -> ubrGeoLocationImportTask.doWork(session.getId()));
    }

    public void markFailedImportSessionsAsInterrupted(String status) {
        importSessionRepository.markInterrupted(status);
    }

    public LocationImportSession getLocationImportSessionById(long sessionId) {
        return importSessionRepository.findById(sessionId).orElse(null);
    }

    public List<HouseholdLocation> getHouseholdLocations(LocationType locationType, Long parentCode) {
        return locationRepository.getHouseholdLocations(locationType, parentCode);
    }
}
