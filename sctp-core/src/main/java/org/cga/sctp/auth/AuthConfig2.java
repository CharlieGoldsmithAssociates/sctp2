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

package org.cga.sctp.auth;

import org.cga.sctp.audit.AuditEventLog;
import org.cga.sctp.audit.DefaultLogMessagePrinter;
import org.cga.sctp.audit.EventType;
import org.cga.sctp.audit.LogMessagePrinter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Configuration
public class AuthConfig2 {

    @Bean
    public LogMessagePrinter generalLogMessagePrinter(){
        return new DefaultLogMessagePrinter(EventType.general){
            @Override
            public String printAuditEventLog(AuditEventLog eventLog) {
                Map<String, Object> logData = eventLog.getLogData();
                String ip = (String) logData.get("ipAddress");
                String what = (String) logData.get("what");

                if (isNotEmpty(what)) {
                    if (isNotEmpty(ip)) {
                        if (!what.endsWith(".")) {
                            what += ".";
                        }
                        what += " IP: " + ip;
                    }
                    return what;
                }
                return "n/a";
            }
        };
    }

    @Bean
    public LogMessagePrinter authEventPrinter() {
        return new DefaultLogMessagePrinter(EventType.authentication) {
            @Override
            public String printAuditEventLog(AuditEventLog eventLog) {
                Map<String, Object> logData = eventLog.getLogData();
                String ip = (String) logData.get("ipAddress");
                String user = (String) logData.get("username");
                String what = (String) logData.computeIfAbsent("what", (Function<String, String>) s -> (String) logData.get("status"));

                if (isNotEmpty(what)) {
                    if (isNotEmpty(user)) {
                        what = user + ": " + what;
                    }
                    if (isNotEmpty(ip)) {
                        if (!what.endsWith(".")) {
                            what += ".";
                        }
                        what += " IP: " + ip;
                    }
                    return what;
                }
                return "n/a";
            }
        };
    }
}
