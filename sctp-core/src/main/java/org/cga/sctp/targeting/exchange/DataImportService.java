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

package org.cga.sctp.targeting.exchange;

import org.cga.sctp.core.TransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DataImportService extends TransactionalService {

    @Autowired
    private DataImportsViewRepository viewRepository;

    @Autowired
    private DataImportRepository importRepository;

    public List<DataImportView> getDataImportsByImporter(Long userId) {
        return viewRepository.findByImporterUserId(userId);
    }

    public List<DataImportView> getDataImports() {
        return viewRepository.findAllOrderByIdDesc(Pageable.unpaged());
    }

    public void saveDataImport(DataImport dataImport) {
        importRepository.save(dataImport);
    }

    public DataImport findImportByIdAndStatus(Long id, DataImportObject.ImportSource importSource, DataImportObject.ImportStatus importStatus) {
        return importRepository.findImportByIdAndDataSourceAndStatus(id, importSource, importStatus);
    }

    public DataImport findPendingUbrCsvImport(Long id) {
        return importRepository.findImportByIdAndDataSourceAndStatus(
                id,
                DataImportObject.ImportSource.UBR_CSV,
                DataImportObject.ImportStatus.FileUploadPending
        );
    }

    public DataImport findDataImportById(Long id) {
        return importRepository.findById(id).orElse(null);
    }

    public DataImportView findImportViewByIdAndStatus(Long id, DataImportObject.ImportSource importSource, DataImportObject.ImportStatus importStatus) {
        return viewRepository.findImportByIdAndDataSourceAndStatus(id, importSource, importStatus);
    }

    public void deleteDataImport(DataImport dataImport) {
        importRepository.delete(dataImport);
    }

    public DataImportView findDataImportViewById(Long id) {
        return viewRepository.findById(id).orElse(null);
    }

    @Transactional
    public void closeImportSession(DataImport dataImport) {
        // Ideally, do all the updates in the stored procedure. But this way it's future proof
        importRepository.save(dataImport);
        importRepository.closeDataImportSession(dataImport.getId());
    }

    public void calculateImportSessionDuplicates(DataImportView dataImport) {
        importRepository.closeDataImportSession(dataImport.getId());
    }

    public void mergeBatchIntoPopulation(DataImportView dataImport) {
        importRepository.mergeBatchIntoPopulation(dataImport.getId(), DataImportObject.ImportStatus.Merged.name());
    }
}
