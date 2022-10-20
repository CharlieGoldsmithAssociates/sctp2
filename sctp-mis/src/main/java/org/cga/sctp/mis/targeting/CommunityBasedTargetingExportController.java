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

package org.cga.sctp.mis.targeting;

import org.cga.sctp.targeting.CbtStatus;
import org.cga.sctp.targeting.TargetingService;
import org.cga.sctp.targeting.TargetingSessionView;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;

@Controller
@RequestMapping("/targeting/community/export")
public class CommunityBasedTargetingExportController {

    @Value("${imports.staging}")
    private String stagingDirectory;

    @Autowired
    private TargetingService targetingService;

    // TODO: ? @GetMapping("/pdf/{session-id}")

    /**
     * Exports full targeting result for the given session to Excel file.
     *
     * @param targetingSessionId the session to export data for
     * @return Response entity, possible to return 404 if session doesn't exist..
     */
    @GetMapping("/excel/{session-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<Resource> generateExcel(@PathVariable("session-id") Long targetingSessionId,
                                                  @RequestParam("status") String status) {
        TargetingSessionView targetingSession = targetingService.findTargetingSessionViewById(targetingSessionId);
        if (targetingSession == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath;
            CbtStatus statusParam;
            try {
                statusParam = CbtStatus.valueOf(status);
                filePath = targetingService.exportSessionDataByStatusToExcel(targetingSession, statusParam, stagingDirectory);
            } catch (IllegalArgumentException e) {
                filePath = targetingService.exportSessionDataToExcel(targetingSession, stagingDirectory);
            }

            return ResponseEntity.status(200)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", String.format("filename=Targeted-Households-%s.xlsx", targetingSessionId))
                    .body(new FileSystemResource(filePath));
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Failed to export beneficiaries", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
