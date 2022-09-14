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

package org.cga.sctp.targeting.exchange;

import org.cga.sctp.core.TransactionalService;
import org.hibernate.validator.internal.util.logging.formatter.DurationFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Component
class ImportMergeService extends TransactionalService {

    @Autowired
    private DataImportRepository importRepository;

    @Async
    @Transactional
    public void mergeHouseholds(Long id) {
        ZonedDateTime start = ZonedDateTime.now();
        LOG.info("starting merge...");
        final DataImport di = importRepository.getById(id);

        di.setStatus(DataImportObject.ImportStatus.Merging);
        di.setStatusText(di.getStatus().description);
        importRepository.save(di);

        importRepository.mergeBatchIntoPopulation(id, DataImportObject.ImportStatus.Merged.name());

        di.setStatusText("Merging completed successfully!");
        di.setStatus(DataImportObject.ImportStatus.Merged);
        di.setMergeDate(ZonedDateTime.now());
        importRepository.save(di);

        ZonedDateTime end = ZonedDateTime.now();
        LOG.info("merging done. duration: {}", new DurationFormatter(Duration.between(start, end)));
        CompletableFuture.completedFuture(null);
    }
}
