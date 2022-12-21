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

package org.cga.sctp.targeting.importation.location;

import com.opencsv.CSVReader;
import org.cga.sctp.utils.LocaleUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class GeoLocationReader implements AutoCloseable, Closeable {
    private final int bufferSize;
    private final CSVReader csvReader;

    public GeoLocationReader(int bufferSize, InputStream inputStream) {
        this.bufferSize = Math.max(100, Math.min(bufferSize, 1000));
        this.csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * Read next records.
     *
     * @return List containing records read. Empty list means no more data
     * @throws Exception .
     */
    public List<GeoLocationImport> readNext() throws Exception {
        if (this.csvReader.getLinesRead() == 0) {
            this.csvReader.skip(1);
        }

        String[] row;
        List<GeoLocationImport> imports = new LinkedList<>();

        do {
            row = this.csvReader.readNext();
            if (row == null) {
                break;
            }
            imports.add(new GeoLocationImport(
                    Long.parseUnsignedLong(row[0]), // code
                    row[1], // name
                    "1".equalsIgnoreCase(row[2]), // active
                    LocaleUtils.isStringNullOrEmpty(row[3])
                            ? null
                            : Long.parseUnsignedLong(row[3]) // parent_code
            ));
        } while (imports.size() != bufferSize);
        return imports;
    }

    @Override
    public void close() throws IOException {
        this.csvReader.close();
    }
}
