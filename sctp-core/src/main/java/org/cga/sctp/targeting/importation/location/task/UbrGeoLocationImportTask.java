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

package org.cga.sctp.targeting.importation.location.task;

import org.cga.sctp.application.SystemInformation;
import org.cga.sctp.core.BaseComponent;
import org.cga.sctp.location.ImportSessionStatus;
import org.cga.sctp.location.LocationImportSession;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.targeting.importation.location.GeoLocationImport;
import org.cga.sctp.targeting.importation.location.GeoLocationReader;
import org.cga.sctp.targeting.importation.location.ImportLocationType;
import org.cga.sctp.targeting.importation.ubrapi.UbrApiConfiguration;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public class UbrGeoLocationImportTask extends BaseComponent {

    private static final String HEADER_LOCATION_COUNT = "X-Location-Count";
    private static final String HEADER_LOCATION_TYPE_NAME = "X-Location-Type-Name";
    private static final String HEADER_LOCATION_TYPE_CODE = "X-Location-Type-Code";

    private final LocationService locationService;

    private final SystemInformation systemInformation;

    private final UbrApiConfiguration apiConfiguration;

    private static final String FETCH_HOUSEHOLDS_TEMPLATE = "%s/api/v1/get_locations_by_type/%d";

    public UbrGeoLocationImportTask(
            LocationService locationService,
            SystemInformation systemInformation,
            UbrApiConfiguration apiConfiguration) {
        this.locationService = locationService;
        this.systemInformation = systemInformation;
        this.apiConfiguration = apiConfiguration;
    }

    private boolean downloadGeoLocations(LocationImportSession importSession, ImportLocationType locationType) {
        String baseUrl = apiConfiguration.getBaseUrl();
        long reportedCount = importSession.getReportedCount(), importedCount = importSession.getImportedCount();

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(apiConfiguration.getClientTimeoutSeconds()))
                .build();

        try {
            String url = String.format(FETCH_HOUSEHOLDS_TEMPLATE, baseUrl, locationType.code);

            LOG.info("Sending request to {}", url);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("User-Agent", format("Target MIS %s", systemInformation.getVersion()))
                    .header("Accept", "*/*")
                    .header("Authorization", "Basic " + apiConfiguration.basicAuthCredentials())
                    .GET()
                    .build();

            var bodyHandler = HttpResponse.BodyHandlers.ofInputStream();
            var response = httpClient.send(httpRequest, bodyHandler);
            var inputStream = response.body();

            var count = response.headers().firstValueAsLong(HEADER_LOCATION_COUNT).orElse(0);
            reportedCount += count;

            LOG.debug("Reported {} count: {}", locationType, count);

            importSession.setReportedCount(reportedCount);

            try (GeoLocationReader reader = new GeoLocationReader(100, inputStream)) {
                List<GeoLocationImport> list;
                while (!(list = reader.readNext()).isEmpty()) {
                    int size = list.size();
                    LOG.debug("got {} of {}", size, locationType);
                    locationService.importLocations(list, locationType);

                    importSession.setImportedCount(importedCount += size);
                }
                locationService.saveLocationImportSession(importSession);
                LOG.debug("");
            }
        } catch (Exception e) {
            LOG.error("Error fetching location data from UBR", e);
            if (e instanceof IOException) {
                importSession.setStatusText(e.getMessage());
            } else {
                importSession.setStatusText("error parsing location data");
            }
            return false;
        }
        return true;
    }

    @Job(name = "UBR Location Downloader", retries = 1)
    @Transactional
    public void doWork(long importSessionId) throws Exception {
        LocationImportSession session = locationService.getLocationImportSessionById(importSessionId);
        if (session == null) {
            LOG.error("Import session {} does not exist", importSessionId);
            return;
        }
        if (session.getStatus() != ImportSessionStatus.Downloading) {
            LOG.error("Import session {} already finished", importSessionId);
            return;
        }
        final ImportLocationType[] types = {
                ImportLocationType.DISTRICT,
                ImportLocationType.TRADITIONAL_AUTHORITY,
                ImportLocationType.GROUP_VILLAGE_HEAD,
                ImportLocationType.CLUSTER,
                ImportLocationType.ZONE,
                ImportLocationType.VILLAGE,
        };
        for (ImportLocationType locationType : types) {
            LOG.info("Downloading locations at level: {}", locationType);
            if (!downloadGeoLocations(session, locationType)) {
                saveUnSuccessful(session);
                return;
            }
        }
        saveSuccessful(session);
    }

    private void saveSuccessful(LocationImportSession importSession) {
        importSession.setEndDate(ZonedDateTime.now());
        importSession.setStatus(ImportSessionStatus.Downloaded);
    }

    private void saveUnSuccessful(LocationImportSession importSession) {
        importSession.setEndDate(ZonedDateTime.now());
        importSession.setStatus(ImportSessionStatus.Error);
        locationService.saveLocationImportSession(importSession);
    }
}
