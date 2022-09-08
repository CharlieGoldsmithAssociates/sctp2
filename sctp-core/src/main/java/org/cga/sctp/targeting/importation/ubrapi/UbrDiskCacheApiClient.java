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

package org.cga.sctp.targeting.importation.ubrapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.core.BaseComponent;
import org.cga.sctp.targeting.importation.ubrapi.data.UbrApiDataResponse;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Flow;

public class UbrDiskCacheApiClient extends BaseComponent implements UbrApiClient {

    private static final String fetchHouseholdsTemplate = "%s/api/v2/get_households_data";

    private final UbrApiConfiguration apiConfiguration;

    private ObjectMapper objectMapper;

    private final Path stagingPath;

    public UbrDiskCacheApiClient(UbrApiConfiguration config, String stagingArea) {
        this.apiConfiguration = config;
        this.stagingPath = Path.of(stagingArea).toAbsolutePath().normalize();
    }

    private String newFileName(UbrRequest request) {
        return format("district_%s_%d.json", request.getDistrictCode(), System.currentTimeMillis());
    }

    private OutputStream createDataStagingJsonFile(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    @Override
    public UbrApiDataResponse fetchNewHouseholds(UbrRequest request) {
        String baseUrl = apiConfiguration.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(apiConfiguration.getClientTimeoutSeconds()))
                .build();

        try {
            String url = String.format(fetchHouseholdsTemplate, baseUrl);
            String requestBody = objectMapper.writeValueAsString(request);
            LoggerFactory.getLogger(getClass()).info("Sending request to {} with following parameters: {}", url, requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("User-Agent", "CGA Target MIS v1.4.10-dev")
                    .header("Accept", "*/*")
                    .header("Authorization", "Basic " + apiConfiguration.basicAuthCredentials())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            final File stagingFile = new File(stagingPath.toFile(), newFileName(request));
            try (OutputStream jsonFile = createDataStagingJsonFile(stagingFile)) {
                HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
                int cached = StreamUtils.copy(response.body(), jsonFile);
                LOG.info("saved data to {}", stagingFile);
            }

            HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

            StreamUtils.copy(response.body(), )

            //HttpResponse.BodyHandlers.ofFile(Paths.get("staging/ubrimport.json"))
            byte[] responseData = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray()).body();
            UbrApiDataResponse ubrApiDataResponse = objectMapper.readValue(responseData, UbrApiDataResponse.class);
            return ubrApiDataResponse;
        } catch (IOException | InterruptedException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to fetch data from UBR", e);
        }
        return null;
    }

    @Override
    public UbrApiDataResponse fetchExistingHouseholds(UbrRequest request) {
        //UbrApiDataResponse resp; resp.
        return null;
    }

    private class DiskCacheHandler implements HttpResponse.BodyHandler<InputStream> {

        @Override
        public HttpResponse.BodySubscriber<InputStream> apply(HttpResponse.ResponseInfo responseInfo) {
            HttpResponse.BodySubscribers.fromSubscriber(new Flow.Subscriber<List<ByteBuffer>>() {
                @Override
                public void onSubscribe(Flow.Subscription subscription) {

                }

                @Override
                public void onNext(List<ByteBuffer> item) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });
            return null;
        }
    }
}
