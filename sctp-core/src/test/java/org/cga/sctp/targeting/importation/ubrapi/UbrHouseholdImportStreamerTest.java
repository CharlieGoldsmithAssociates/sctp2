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
import org.cga.sctp.targeting.importation.ubrapi.data.TargetingData;
import org.cga.sctp.utils.LocaleUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

public class UbrHouseholdImportStreamerTest {

    private static final String ENV_KEY = "UBR_IMPORT_JSON_TEST_FILE";

    @Test
    public void testImport() {
        Logger logger = LoggerFactory.getLogger(getClass());
        String testFile = System.getenv().get(ENV_KEY);
        if (LocaleUtils.isStringNullOrEmpty(testFile)) {
            logger.info("skipping this test because {} was not specified in env", ENV_KEY);
            return;
        }
        File file = new File(testFile);
        ObjectMapper mapper = new ObjectMapper();
        AtomicLong totalMembers = new AtomicLong();
        new UbrHouseholdImportStreamer(file, mapper)
                .batchsize(4) // batch size
                .total(-1) // read 16 records only
                .completed(total -> logger.info("completed with {} households and {} members. Avg members/household: {}", total, totalMembers, average(total, totalMembers.get())))
                .erroroccurred(error -> logger.warn("there was an error on UBR side. No data will be imported"))
                .exception(e -> logger.error("Exception when reading data from json file", e))
                .stream(targetingDataList -> {
                    int households = targetingDataList.size();
                    long members = targetingDataList.stream()
                            .flatMap((Function<TargetingData, Stream<?>>) targetingData -> targetingData.household_members.stream()).count();
                    totalMembers.addAndGet(members);
                    logger.info("read {} households with {} total member(s) in total", households, members);
                });
    }

    long average(long households, long members) {
        BigDecimal hh = BigDecimal.valueOf(households);
        BigDecimal hhm = BigDecimal.valueOf(members);
        return hhm.divide(hh, RoundingMode.HALF_EVEN).longValue();
    }
}