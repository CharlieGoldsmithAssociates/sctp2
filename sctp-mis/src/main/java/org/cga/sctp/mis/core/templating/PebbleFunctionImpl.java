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

package org.cga.sctp.mis.core.templating;

import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import org.unbescape.html.HtmlEscape;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class PebbleFunctionImpl implements Function {
    private final String name;
    private final List<String> args;
    private final EscapingStrategy escapingStrategy = HtmlEscape::escapeHtml4Xml;

    protected PebbleFunctionImpl(String name, List<String> args) {
        this.name = name;
        this.args = args;
    }

    protected PebbleFunctionImpl(String name) {
        this(name, List.of());
    }

    public final String getName() {
        return name;
    }

    protected final boolean isNullOrEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof String) {
            return ((String) object).isBlank();
        }
        return false;
    }

    protected final String escape(String input) {
        return escapingStrategy.escape(input);
    }

    @SuppressWarnings("unchecked")
    protected final <T> T getArgument(List<Object> args, int index, T defaultValue) {
        int size = args.size();
        if (index < size) {
            return (T) args.get(index);
        }
        return defaultValue;
    }

    protected final boolean isNull(Object object) {
        return object == null;
    }

    @SuppressWarnings("unchecked")
    protected final <T> T getArgument(Map<String, Object> args, String name, T defaultValue) {
        final Object object = args.get(name);
        if (isNull(object)) {
            return defaultValue;
        }
        return (T) object;
    }

    protected final Collection<Object> extractArgs(Map<String, Object> args, boolean throwIfEmpty) {
        if (args.isEmpty()) {
            if (throwIfEmpty) {
                throw new IllegalArgumentException("Function requires arguments but none were specified.");
            }
            return List.of();
        }
        return args.values();
    }

    protected final Collection<Object> extractArgs(Map<String, Object> args) {
        return extractArgs(args, false);
    }

    @Override
    public final List<String> getArgumentNames() {
        return args;
    }

    @Override
    public String toString() {
        return getName();
    }
}
