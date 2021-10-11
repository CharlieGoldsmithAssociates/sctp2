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

package org.cga.sctp.mis.targeting.importation.conversion;

import com.univocity.parsers.conversions.Conversion;
import org.cga.sctp.utils.LocaleUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionConversion implements Conversion<String, Collection<String>> {
    private static final String SEPARATOR_COMMA = ",";

    public enum CollectionType {
        Set,
        List
    }

    private final String separator;
    private final CollectionType collectionType;

    public CollectionConversion(CollectionType type, String separator) {
        this.collectionType = type;
        this.separator = separator;
    }

    public CollectionConversion() {
        this(CollectionType.List, SEPARATOR_COMMA);
    }

    public CollectionConversion(String[] args) {
        this.collectionType = CollectionType.valueOf(args[0]);
        this.separator = args.length >= 2 ? args[1] : SEPARATOR_COMMA;
    }

    @Override
    public Collection<String> execute(String input) {
        if (LocaleUtils.isStringNullOrEmpty(input)) {
            return switch (collectionType) {
                case Set -> Collections.EMPTY_SET;
                case List -> Collections.EMPTY_LIST;
            };
        }
        final String[] values = input.split(separator);
        return switch (collectionType) {
            case Set -> Set.of(values).stream().filter(StringUtils::hasText).collect(Collectors.toSet());
            case List -> List.of(values).stream().filter(StringUtils::hasText).collect(Collectors.toList());
        };
    }

    @Override
    public String revert(Collection<String> input) {
        if (input.isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(separator);
        for (String item : input) {
            joiner.add(item);
        }
        return joiner.toString();
    }
}
