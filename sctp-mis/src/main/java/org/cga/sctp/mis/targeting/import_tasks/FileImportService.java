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

import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.exchange.DataImport;
import org.cga.sctp.targeting.exchange.DataImportService;
import org.cga.sctp.targeting.importation.FileImportTaskView;
import org.cga.sctp.targeting.importation.ImportTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Service
public class FileImportService extends TransactionalService {

    @Autowired
    private FileImportConfig config;

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private ImportTaskService importTaskService;

    @Autowired
    private ExecutorService importTaskExecutor;

    private Path stagingPath;

    private BlockingQueue<ImportTaskParameter> taskParameterQueue;
    private FileImportJob fileImportJob;

    @PostConstruct
    private void init() throws Exception {
        stagingPath = config.getStagingDirectory().getAbsoluteFile().toPath();
        Files.createDirectories(stagingPath);
    }

    @EventListener(ApplicationReadyEvent.class)
    void startBackgroundThreads() {
        fileImportJob = new FileImportJob(
                taskParameterQueue = new LinkedBlockingQueue<>(),
                stagingPath,
                this,
                importTaskService,
                dataImportService
        );

        // initially load tasks from database
        List<FileImportTaskView> tasks = importTaskService.getQueuedImportTaskViews();
        if (!tasks.isEmpty()) {
            taskParameterQueue.addAll(tasks.stream().map(ImportTaskParameter::new).collect(Collectors.toList()));
        }

        importTaskExecutor.submit(fileImportJob);
    }

    public synchronized void stopImportTask() {
        if (fileImportJob != null) {
            fileImportJob.shutdown();
        }
    }

    public void queueImport(DataImport dataImport) {
        FileImportTaskView view = importTaskService.createTaskFromDataImport(dataImport);

        ImportTaskParameter taskParameter = new ImportTaskParameter(view);

        if (!taskParameterQueue.offer(taskParameter)) {
            LOG.warn("Memory queue is full at {}", taskParameterQueue.size());
            // TODO Add another thread to periodically monitor the queue and load from DB If items ever reach
            //  (Integer.MAX) (most unlikely)
        }
    }

    public Resource getUbrHouseholdTemplate() {
        return config.getUbrHouseholdTemplate();
    }
}