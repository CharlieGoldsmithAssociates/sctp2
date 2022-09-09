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

package org.cga.sctp.mis.targeting.import_tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.exchange.DataImport;
import org.cga.sctp.targeting.exchange.DataImportObject;
import org.cga.sctp.targeting.exchange.DataImportService;
import org.cga.sctp.targeting.importation.ImportTaskService;
import org.cga.sctp.targeting.importation.UbrHouseholdImport;
import org.cga.sctp.targeting.importation.ubrapi.*;
import org.cga.sctp.targeting.importation.ubrapi.data.TargetingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles importing of data from UBR API
 * <p>
 * TODO: Keep a list of running jobs to prevent/avoid duplicate jobs
 * TODO: Use taskqueue similarly to how it's done in FileImportService
 * TODO: Re-use functionality between this class and FileImportService
 */
@Service
public class UBRImportService extends TransactionalService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private ImportTaskService importTaskService;

    @Autowired
    private UbrApiClient ubrApiClient;

    private ExecutorService importTaskExecutor = Executors.newSingleThreadExecutor();

    private BlockingQueue<Object> taskParameterQueue;

    public DataImportService getDataImportService() {
        return dataImportService;
    }

    public void setDataImportService(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    public ImportTaskService getImportTaskService() {
        return importTaskService;
    }

    public void setImportTaskService(ImportTaskService importTaskService) {
        this.importTaskService = importTaskService;
    }

    private static final UbrApiDataToHouseholdImportMapper mapper = new UbrApiDataToHouseholdImportMapper();

    public DataImport queueImportFromUBRAPI(final UbrRequest ubrRequest, final long userId) {
        ubrRequest.setProgrammes(UbrRequest.UBR_SCTP_PROGRAMME_CODE);

        String importSessionTitle = format("%s - %s @%s", ubrRequest.getDistrictCode(), ubrRequest.getTraditionalAuthorityCode(), LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        DataImport dataImport = new DataImport();
        dataImport.setTitle(importSessionTitle);
        dataImport.setDataSource(DataImportObject.ImportSource.UBR_API);
        dataImport.setImporterUserId(userId);
        dataImport.setCompletionDate(null);
        dataImport.setBatchDuplicates(0L);
        dataImport.setHouseholds(0L);
        dataImport.setIndividuals(0L);
        dataImport.setNewHouseholds(0L);
        dataImport.setOldHouseholds(0L);
        dataImport.setPopulationDuplicates(0L);
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(ubrRequest);
            dataImport.setSourceFile(requestJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process request data", e);
        }
        dataImport.setStatus(DataImportObject.ImportStatus.FileUploadPending);
        dataImport.setStatusText("Waiting for API Data Fetch");
        dataImport.setImportDate(LocalDateTime.now());

        // Save to the database first before we start processing it in the background
        dataImportService.saveDataImport(dataImport);

        publishGeneralEvent("User:%s initiated Import from UBR API with parameters: %s", userId, requestJson);

        dataImport.setStatus(DataImportObject.ImportStatus.Processing);
        dataImport.setStatusText("Downloading data from UBR...");

        // TODO: use the taskParameterQueue similarly to how it's being used in the FileImportService
        importTaskExecutor.submit(() -> {
            LOG.info("Initiating import from UBR using {}", ubrRequest);
            // TODO: initiate the data import in another thread via the import service
            UbrApiClientRequestResult result = ubrApiClient.fetchExistingHouseholds(ubrRequest);
            if (result.error()) {
                dataImport.setStatusText(result.getStatus());
                dataImport.setStatus(DataImportObject.ImportStatus.Error);
                dataImportService.saveDataImport(dataImport);
                publishGeneralEvent("Data Import UBR API FAILED with parameters: %s, initiated by User:%s",
                        ubrRequest, dataImport.getImporterUserId());
                return dataImport;
            }

            // import data in streaming mode off the disk, in chunks
            final long importBatchNumber = Long.parseLong(
                    format("%d%d%d", dataImport.getId(), dataImport.getImporterUserId(), System.currentTimeMillis())
            );
            UbrHouseholdImportStreamer streamer = new UbrHouseholdImportStreamer(result.getFile(), objectMapper);
            streamer.batchsize(10)
                    .total(-1) // read all records
                    .erroroccurred(args -> {
                        dataImport.setStatusText("UBR server reported an internal error");
                        dataImport.setStatus(DataImportObject.ImportStatus.Error);
                    })
                    .sizeavailable(size -> {
                        dataImport.setStatusText(format("Importing %,d household(s) into temporary table...", size));
                        dataImportService.saveDataImport(dataImport);
                    })
                    .exception(args -> {
                        LOG.error("Exception during import from disk", args);
                        dataImport.setStatusText("Data was downloaded from UBR successfully but could not be imported into temporary table");
                        dataImport.setStatus(DataImportObject.ImportStatus.Error);
                    })
                    .completed(total -> {
                        dataImport.setCompletionDate(LocalDateTime.now());
                        if (dataImport.getStatus() == DataImportObject.ImportStatus.Processing) {
                            dataImport.setStatusText(format("Successfully imported %,d household(s)", total));
                            dataImport.setStatus(DataImportObject.ImportStatus.Review);
                            dataImportService.closeImportSession(dataImport);
                            publishGeneralEvent("Data Import Complete from UBR API with parameters: %s, initiated by User:%s",
                                    ubrRequest, dataImport.getImporterUserId());
                        } else {
                            dataImportService.saveDataImport(dataImport);
                        }
                    })
                    .stream(args -> {
                        for (TargetingData targetingData : args) {
                            List<UbrHouseholdImport> imports
                                    = mapper.interpolateTargetingData(dataImport, targetingData, importBatchNumber);
                            importTaskService.saveImports(imports);
                        }
                    });

            return dataImport;
        });

        return dataImport;
    }
}
