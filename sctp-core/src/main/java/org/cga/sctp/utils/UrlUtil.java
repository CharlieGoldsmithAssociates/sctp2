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

package org.cga.sctp.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author kamfosic
 * @date June 3, 2022
 * <p>
 * Utility class for manipulating URLS
 */
public class UrlUtil {
    private final URL url;
    private final Map<String, List<String>> parameters;

    public UrlUtil(String url) throws MalformedURLException {
        this.url = new URL(url);
        this.parameters = new LinkedHashMap<>();
        parse();
    }

    private List<String> getParameterValues(String parameter) {
        return this.parameters.computeIfAbsent(parameter, p -> new LinkedList<>());
    }

    private void addParameterWithValue(String parameter, String value) {
        boolean emptyValue = value == null || value.isBlank();
        List<String> values = this.getParameterValues(parameter);
        if (!emptyValue) {
            values.add(value);
        }
    }

    private void parse() {
        String query = url.getQuery();
        if (query != null) {
            String[] parts = query.split("&");
            if (parts.length > 0) {
                for (String pair : parts) {
                    String[] map = pair.split("=");
                    addParameterWithValue(map[0], map.length == 2 ? map[1] : null);
                }
            }
        }
    }

    public UrlUtil addParameter(String parameter, String value) {
        addParameterWithValue(parameter, value);
        return this;
    }

    public UrlUtil replaceSingleParameter(String parameter, Object value) {
        List<String> values = this.getParameterValues(parameter);
        String objString = Objects.toString(value);
        if (values.isEmpty()) {
            values.add(objString);
        } else {
            values.set(0, objString);
        }
        return this;
    }

    public UrlUtil setParameter(String parameter, Object value) {
        String objString = Objects.toString(Objects.requireNonNull(value));
        List<String> values = this.getParameterValues(parameter);
        int index = values.indexOf(objString);
        if (index == -1) {
            values.add(objString);
        } else {
            values.set(index, objString);
        }
        return this;
    }

    public String getParameter(String name) {
        List<String> values = parameters.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : "";
    }

    private void buildQuery(StringBuilder builder) {
        StringJoiner joiner = new StringJoiner("&");
        if (!this.parameters.isEmpty()) {
            for (String name : parameters.keySet()) {
                // k-> [1,2,3,4]
                // k=1&k=2&k=3&k=4
                List<String> values = this.parameters.get(name);
                if (values.isEmpty()) {
                    joiner.add(name + '=');
                } else {
                    for (String value : values) {
                        joiner.add(name + '=' + value);
                    }
                }
            }
            builder.append('?').append(joiner);
        }
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        // <scheme>://<authority><path>?<query>#<fragment>
        String scheme = url.getProtocol();
        String authority = url.getAuthority();
        String path = url.getPath();
        String fragment = url.getRef();

        builder.append(scheme)
                .append("://")
                .append(authority);
        if (!path.isBlank()) {
            builder.append(path);
        }
        buildQuery(builder);
        if (fragment != null) {
            builder.append('#').append(fragment);
        }
        return builder.toString();
    }

    public URL buildURL() throws MalformedURLException {
        return new URL(build());
    }

    /**
     * Remove parameter with specified value. Other occurrences with different values will be left intact
     *
     * @param parameter .
     * @param value     .
     */
    public void removeParameter(String parameter, String value) {
        List<String> values = this.parameters.get(parameter);
        if (values != null) {
            values.remove(value);
            if (values.isEmpty()) {
                this.parameters.remove(parameter);
            }
        }
    }

    /**
     * Remove all occurrences of a parameter.
     *
     * @param parameter .
     */
    public UrlUtil removeParameter(String parameter) {
        this.parameters.remove(parameter);
        return this;
    }

    public boolean hasQueryParameter(String name) {
        return this.parameters.containsKey(name);
    }

    public boolean hasParameterWithValue(String name, String value) {
        List<String> values = this.parameters.get(name);
        if (values != null && !values.isEmpty()) {
            return values.contains(value);
        }
        return false;
    }

    public static String getRequestPath(URL url) {
        String path = url.getPath();
        String query = url.getQuery();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (query != null) {
            query = '?' + query;
        }
        return path + query;
    }

    @Override
    public String toString() {
        return url.toString();
    }
}