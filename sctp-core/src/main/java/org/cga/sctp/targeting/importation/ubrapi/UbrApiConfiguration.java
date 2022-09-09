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

import org.cga.sctp.core.BaseComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class UbrApiConfiguration extends BaseComponent {

    private final String baseUrl;

    private final String username;

    private final String password;

    private final int clientTimeoutSeconds;

    private final File tmp;

    public UbrApiConfiguration(@Value("${ubr.api.baseUrl}") String baseUrl,
                               @Value("${ubr.api.username}") String username,
                               @Value("${ubr.api.password}") String password,
                               @Value("${ubr.api.timeout:300}") int clientTimeoutSeconds,
                               @Value("${ubr.api.tmp:./staging}") File tmp) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.clientTimeoutSeconds = clientTimeoutSeconds;
        this.tmp = tmp.getAbsoluteFile().toPath().normalize().toFile();

        if (!this.tmp.isDirectory()) {
            if (!this.tmp.mkdirs()) {
                LOG.error("failed to create temporary directory for UBR imports on {}", tmp.getAbsolutePath());
            }
        }
    }

    public File getTmp() {
        return tmp;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getClientTimeoutSeconds() {
        return clientTimeoutSeconds;
    }

    public String basicAuthCredentials() {
        var encoder = Base64.getEncoder();
        return encoder.encodeToString((getUsername() + ":" + getPassword()).getBytes(StandardCharsets.UTF_8));
    }
}
