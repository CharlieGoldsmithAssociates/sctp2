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

package org.cga.sctp.targeting.importation;

import java.time.LocalDateTime;

class FileImportTaskViewWrapper extends FileImportTaskView {
    private final String sourceFile;
    private final FileImportTask delegate;

    FileImportTaskViewWrapper(String sourceFile, FileImportTask delegate) {
        this.sourceFile = sourceFile;
        this.delegate = delegate;
        super.setSourceFile(sourceFile);
    }

    FileImportTask getDelegate() {
        return delegate;
    }

    @Override
    public void setFinishedAt(LocalDateTime finishedAt) {
        delegate.setFinishedAt(finishedAt);
    }

    @Override
    public void setCurrentRow(Long currentRow) {
        delegate.setCurrentRow(currentRow);
    }

    @Override
    public void setRowCount(Long rowCount) {
        delegate.setRowCount(rowCount);
    }

    @Override
    public void setStatus(ImportTaskStatus status) {
        delegate.setStatus(status);
    }

    @Override
    public Long getDataImportId() {
        return delegate.getDataImportId();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return delegate.getCreatedAt();
    }

    @Override
    public LocalDateTime getFinishedAt() {
        return delegate.getFinishedAt();
    }

    @Override
    public Long getCurrentRow() {
        return delegate.getCurrentRow();
    }

    @Override
    public Long getRowCount() {
        return delegate.getRowCount();
    }

    @Override
    public Long getId() {
        return delegate.getId();
    }

    @Override
    public ImportTaskStatus getStatus() {
        return delegate.getStatus();
    }

    public String getSourceFile() {
        return sourceFile;
    }
}
