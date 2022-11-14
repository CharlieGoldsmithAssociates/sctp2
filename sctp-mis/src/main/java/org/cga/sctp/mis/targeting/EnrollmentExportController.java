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

import org.cga.sctp.core.BaseComponent;
import org.cga.sctp.targeting.enrollment.EnrollmentService;
import org.cga.sctp.targeting.enrollment.EnrollmentSessionView;
import org.cga.sctp.targeting.enrollment.EnrollmentUpdateForm;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/targeting/enrollment/export")
public class EnrollmentExportController extends BaseComponent {

    @Autowired
    private EnrollmentService enrollmentService;


    @GetMapping("/excel/{session-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<Resource> exportEnrollmentList(@PathVariable("session-id") Long sessionId,
                                                         @RequestParam("status") Optional<EnrollmentUpdateForm.EnrollmentStatus> status) {
        EnrollmentSessionView session = enrollmentService.getEnrollmentSession(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        final Resource resource = enrollmentService.exportEnrollmentList(session, status.orElse(null));

        if (resource == null) {
            return ResponseEntity.internalServerError().build();
        } else {
            return ResponseEntity.status(200)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", format("filename=%s", resource.getFilename()))
                    .body(resource);
        }
    }
}
