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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.targeting.importation.ubrapi.data.TargetingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class UbrHouseholdImportStreamer {
    private int batch = 1;
    private long total = -1;
    private long read = 0;
    private int offset = 0;
    private final AtomicBoolean dirty = new AtomicBoolean(false);
    private Function<Void> error;
    private Function<Long> records;
    private Function<Exception> except = null;
    private final ObjectMapper objectMapper;

    private final File path;
    private Function<Long> end;

    private static final Logger logger = LoggerFactory.getLogger(UbrHouseholdImportStreamer.class);

    public UbrHouseholdImportStreamer(File path, ObjectMapper mapper) {
        this.path = path;
        this.objectMapper = mapper;
    }


    /**
     * <p>Set the batch size for each read. The {@link #stream(Function)} will return at most this many
     * records on each read.</p>
     * <p></p>
     * <p><font size="5" color="red">WARNING:</font> Care must be taken here as this number has memory
     * cost implications because it determines how many
     * records will be held in memory upon each read.</p>
     * <p></p>
     * <p>Based on tests, a batch size of 4 {@link TargetingData} objects yielded an average
     * of 20 to 25 {@link org.cga.sctp.targeting.importation.UbrHouseholdImport}. But that's just because of the
     * average household composition</p>
     *
     * @param size (default 1) Must be >= 1.
     * @return this
     * @throws IllegalArgumentException if size is less than 1
     */
    public UbrHouseholdImportStreamer batchsize(int size) throws IllegalArgumentException {
        if (size < 1) {
            throw new IllegalArgumentException("size must be at least 1");
        }
        this.batch = size;
        adjustBatchSize();
        return this;
    }

    /**
     * <p>Set function to be called when the size of the households becomes available</p>
     *
     * @param fn f.
     * @return this
     */
    public UbrHouseholdImportStreamer sizeavailable(Function<Long> fn) {
        this.records = fn;
        return this;
    }

    /**
     * Set the total amount of records to read. This will correspond to the {@link #batchsize(int)}.
     * <p>{@link  #stream(Function)} will read until the total records read reaches this number or the end
     * of the stream is reached.</p>
     * <br>
     * <p><font color="red">NOTE</font>: This will adjust the {@link #batchsize(int)} if total less than the set
     * batch size, if batch size is greater than zero</p>
     *
     * @param total (default -1) Total records to read from stream. <= 0 until the stream ends.
     * @return this
     */
    public UbrHouseholdImportStreamer total(long total) {
        this.total = total;
        adjustBatchSize();
        return this;
    }

    private void adjustBatchSize() {
        if (this.total >= 1) {
            if (this.batch > this.total) {
                this.batchsize((int) this.total);
            }
        }
    }

    /**
     * Called when the progress is completed, regardless of errors and exceptions.
     *
     * @param fn Callback
     * @return this
     */
    public UbrHouseholdImportStreamer completed(Function<Long> fn) {
        this.end = fn;
        return this;
    }

    /**
     * Called when an exception is encountered. Processing stops and
     * {@link  #completed(Function)} will be called after this
     *
     * @param fn Callback
     * @return this
     */
    public UbrHouseholdImportStreamer exception(Function<Exception> fn) {
        this.except = fn;
        return this;
    }

    /**
     * <p>Called when the <code>errors_occurred</code> field is set to true in the json structure, indicating that there
     * was an error at the UBR.</p>
     * <p>Processing stops and {@link #completed(Function)} will be called next</p>
     *
     * @param fn Callback
     * @return this
     */
    public UbrHouseholdImportStreamer erroroccurred(Function<Void> fn) {
        this.error = fn;
        return this;
    }

    /**
     * @return Checks whether {@link #stream(Function)} can be called.
     */
    public boolean isDirty() {
        return dirty.get();
    }

    /**
     * <p>Begin streaming data from the source.</p>
     *
     * @param fn Callback to receive streamed data. {@link Function#apply(Object)} will
     *           return a list whose items will not exceed the value set by {@link #batchsize(int)}
     * @throws IllegalStateException if this instance has already been significantly used already.
     *                               Call {@link #isDirty()} to verify
     */
    public void stream(@NonNull Function<List<TargetingData>> fn) throws IllegalStateException {
        if (dirty.get()) {
            throw new IllegalStateException("Stream is already closed");
        }
        dirty.set(true);
        Objects.requireNonNull(fn, "Callback function cannot be null");
        boolean emptyRecords = false;
        try (JsonParser parser = objectMapper.getFactory().createParser(path)) {
            expectToken(parser, JsonToken.START_OBJECT);
            for (int i = 0; i < 3; i++) {
                String name = parser.nextFieldName();
                switch (name) {
                    case "error_occurred" -> {
                        if (parser.nextBooleanValue()) {
                            applyFunction(this.error, null);
                            return;
                        }
                    }
                    case "total_records" -> {
                        long totalRecords = expectToken(parser, JsonToken.VALUE_NUMBER_INT).getLongValue();
                        applyFunction(this.records, totalRecords);
                        if (totalRecords == 0) {
                            emptyRecords = true;
                        }
                    }
                    case "targeting_data" -> {
                        if (emptyRecords) {
                            logger.warn("found no households");
                            break;
                        }
                        expectToken(parser, JsonToken.START_ARRAY);
                        expectToken(parser, JsonToken.START_OBJECT);
                        while ((this.total <= 0 || this.offset < this.total)) {
                            applyFunction(fn, readHouseholdData(batch, parser));
                            this.offset += batch;
                            this.read += batch;
                            JsonToken token = parser.nextToken();
                            if (token == null || token == JsonToken.END_OBJECT) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            applyFunction(this.except, e);
        } finally {
            applyFunction(this.end, this.read);
        }
    }

    private <T> void applyFunction(Function<T> func, T args) {
        try {
            if (func != null) {
                func.apply(args);
            }
        } catch (Exception e) {
            logger.error("exception when invoking callback function", e);
        }
    }

    private List<TargetingData> readHouseholdData(int size, JsonParser reader) throws IOException {
        List<TargetingData> imports = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            final TargetingData targetingData = this.objectMapper.readValue(reader, TargetingData.class);
            if (targetingData == null) { // we may have reached the end of the stream
                break;
            }
            imports.add(targetingData);
        }
        return imports;
    }

    private JsonParser expectToken(JsonParser parser, JsonToken token) throws IOException {
        JsonToken found = parser.nextToken();
        if (found != token) {
            throw new JsonParseException(parser, "Expected " + token + " but found " + found);
        }
        return parser;
    }

    public interface Function<T> {
        void apply(T args) throws Exception;
    }
}
