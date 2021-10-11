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

import com.univocity.parsers.common.DataProcessingException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.RowProcessorErrorHandler;
import com.univocity.parsers.common.processor.RowProcessor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class RowTrackingProcessor implements RowProcessor, RowProcessorErrorHandler {
    private long totalRows;
    private long currentRow;
    private long faultyColumns;
    private List<String> errors;

    RowTrackingProcessor() {
        errors = new LinkedList<>();
        totalRows = currentRow = faultyColumns = 0;
    }

    @Override
    public void processStarted(ParsingContext context) {
    }

    @Override
    public void rowProcessed(String[] row, ParsingContext context) {
        totalRows++;
        currentRow = context.currentRecord();
    }

    @Override
    public void processEnded(ParsingContext context) {
        currentRow = context.currentRecord();
    }

    @Override
    public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
        faultyColumns++;
        currentRow = context.currentRecord();

        String columnName = error.getColumnName();

        if (columnName == null) {
            columnName = Objects.toString(error.getValue());
        }

        String detailMessage = error.toString().split("\n")[0];
        String exceptionClassName = DataProcessingException.class.getCanonicalName();
        int index = detailMessage.indexOf(exceptionClassName);
        if (index != -1) {
            detailMessage = detailMessage.substring(exceptionClassName.length());
        }

        errors.add(columnName + " " + detailMessage);
    }

    public List<String> getRecordErrors() {
        List<String> errorList;
        if (errors.isEmpty()) {
            errorList = Collections.emptyList();
        } else {
            errorList = new LinkedList<>(errors);
        }
        errors.clear();
        return errorList;
    }

    public long getCurrentRow() {
        return currentRow;
    }

    public long getFaultyColumns() {
        return faultyColumns;
    }

    /**
     * Returns rows that have been successfully processed
     *
     * @return
     */
    public long getTotalRows() {
        return totalRows;
    }
}
