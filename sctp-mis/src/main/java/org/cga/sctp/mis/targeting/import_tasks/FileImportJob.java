/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
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

import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvRoutines;
import org.cga.sctp.core.BaseComponent;
import org.cga.sctp.targeting.exchange.DataImport;
import org.cga.sctp.targeting.exchange.DataImportObject;
import org.cga.sctp.targeting.exchange.DataImportService;
import org.cga.sctp.targeting.importation.*;
import org.cga.sctp.utils.LocaleUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

final class FileImportJob extends BaseComponent implements Runnable {

    private static class PoisonPill extends ImportTaskParameter {
        private static final PoisonPill INSTANCE = new PoisonPill();
    }

    private final BlockingQueue<ImportTaskParameter> parameterQueue;
    private final AtomicBoolean shutdownFlag = new AtomicBoolean(false);
    private Thread handle;
    private final Path stagingPath;
    private final FileImportService importService;
    private final ImportTaskService taskService;
    private final DataImportService dataImportService;

    FileImportJob(BlockingQueue<ImportTaskParameter> parameterQueue, Path stagingPath, FileImportService importService,
                  ImportTaskService taskService, DataImportService dataImportService) {
        this.stagingPath = stagingPath;
        this.importService = importService;
        this.taskService = taskService;
        this.dataImportService = dataImportService;

        this.parameterQueue = parameterQueue;
    }

    private boolean shouldShutdown() {
        return shutdownFlag.get();
    }

    public void shutdown() {
        if (shutdownFlag.compareAndSet(false, true)) {
            parameterQueue.clear();
            parameterQueue.offer(PoisonPill.INSTANCE);
            if (handle != null) {
                handle.interrupt();
            }
        }
    }

    @Override
    public void run() {
        handle = Thread.currentThread();
        handle.setName(getClass().getCanonicalName());
        LOG.debug("Starting processor thread");

        while (true) {
            try {
                ImportTaskParameter jobParameter = parameterQueue.take();
                if (jobParameter instanceof PoisonPill) {
                    LOG.debug("Found poison pill. Exiting thread");
                    break;
                }
                process(jobParameter);
            } catch (Exception e) {
                if (e instanceof InterruptedException && shouldShutdown()) {
                    LOG.debug("Interrupt flag set. Exiting thread.");
                    break;
                } else {
                    LOG.error("Exception in processor thread", e);
                }
            }
        }

        LOG.debug("Processor thread shutdown");
    }

    /**
     * Process a record: Resolve other values/fields and verify that all required values are set.
     *
     * @param record .
     */
    private void processRecord(UbrHouseholdImport record) {
        UbrHouseholdImportUtil.updateAssetsLiveliHoodAndValidationErrors(record);
    }

    private void process(ImportTaskParameter parameter) {
        File file;
        Long batchNumber;
        int batchSize = 15;
        CsvRoutines routines;
        DataImport dataImport;
        FileImportTaskView task;
        CsvParserSettings settings;
        RowTrackingProcessor processor;
        List<UbrHouseholdImport> batch;

        settings = new CsvParserSettings();
        routines = new CsvRoutines(settings);
        processor = new RowTrackingProcessor();

        task = parameter.getTaskView();
        settings.setProcessor(processor);
        settings.setProcessorErrorHandler(processor);

        settings.setHeaderExtractionEnabled(true);
        settings.setNumberOfRowsToSkip(task.getCurrentRow());

        // Create a buffering batch
        batch = new LinkedList<>();
        file = stagingPath.resolve(task.getSourceFile()).toFile();

        // Preload import session
        dataImport = dataImportService.findDataImportById(task.getDataImportId());

        if (dataImport == null) {
            taskService.deleteFileImportTask(task);
            LOG.warn("Cannot complete import. Parent session with id {} not found.", task.getDataImportId());
            cleanupFile(file.toPath());
            return;
        }

        batchNumber = Long.parseLong(format("%d%d%d",
                dataImport.getImporterUserId(), System.currentTimeMillis(), dataImport.getId()));
        long count = 0, current = 0;
        try {
            for (UbrHouseholdImport record : routines.iterate(UbrHouseholdImport.class, file)) {
                List<String> errors = processor.getRecordErrors();
                if (record == null) {
                    record = new UbrHouseholdImport();
                    record.setErrors(errors);
                    record.setAssets(Collections.emptySet());
                    record.setValidationStatus(UbrHouseholdImport.ValidationStatus.Error);
                } else {
                    // inspect for errors
                    record.setErrors(errors);
                    processRecord(record);
                }

                record.setBatchNumber(batchNumber);
                record.setCreatedAt(LocalDateTime.now());
                record.setDataImportId(dataImport.getId());

                // add to batch database
                batch.add(record);

                if (batch.size() >= batchSize) {
                    taskService.saveImports(batch);
                    batch.clear();
                }

                task.setRowCount(++count);
                task.setCurrentRow(current++);
            }

            // Items remaining in batch ?
            if (!batch.isEmpty()) {
                taskService.saveImports(batch);
            }

            // Send to review
            dataImport.setStatusText("Waiting for review");
            dataImport.setStatus(DataImportObject.ImportStatus.Review);
        } catch (Exception e) {
            LOG.error("Error importing data", e);

            dataImport.setStatusText("Error parsing file");
            dataImport.setStatus(DataImportObject.ImportStatus.Error);
        }

        // Update task information
        task.setFinishedAt(LocalDateTime.now());
        task.setStatus(BaseFileImportTask.ImportTaskStatus.Done);

        taskService.updateTaskWithView(task);

        // Finish the session
        dataImport.setCompletionDate(task.getFinishedAt());
        dataImportService.closeImportSession(dataImport);

        cleanupFile(file.toPath());
    }

    private void cleanupFile(Path path) {
        try {
            Files.delete(path.toAbsolutePath());
        } catch (Exception e) {
            LOG.error("Error removing staged file {}", path);
        }
    }
}
