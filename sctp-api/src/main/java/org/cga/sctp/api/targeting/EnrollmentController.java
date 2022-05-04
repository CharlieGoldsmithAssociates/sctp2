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

package org.cga.sctp.api.targeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.api.core.IncludeGeneralResponses;
import org.cga.sctp.targeting.EnrollmentService;
import org.cga.sctp.targeting.enrollment.EnrollmentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/targeting/enrollment")
public class EnrollmentController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    @RequestMapping(value = "/{session-id}/enroll/single-household", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IncludeGeneralResponses
    public ResponseEntity<Object> postEnrollHouseholds(@PathVariable("session-id") Long sessionId,
                                                       @RequestParam(required = true, value = "file") MultipartFile file,
                                                       @RequestParam(required = false, value = "altPhoto") MultipartFile alternate,
                                                       @RequestParam(required = true, value = "jsondata") String jsondata)
            throws IOException {

        EnrollmentForm enrollmentForm = objectMapper.readValue(jsondata, EnrollmentForm.class);
        // TODO: How do we get the image files? Do we request Base64 encoded images??
        enrollmentService.processEnrollment(enrollmentForm, file, alternate);
        // TODO: return type
        return new ResponseEntity<>("File is uploaded successfully", HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping(value = "/{session-id}/enroll/bulk", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @IncludeGeneralResponses
    public ResponseEntity<Object> postEnrollHouseholdsBulk(@PathVariable("session-id") Long sessionId,
                                                           @RequestBody BulkEnrollmentForm request)
            throws IOException {

        for (EnrollmentForm enrollmentForm: request.getItems()) {
            // TODO: How do we get the image files? Do we request Base64 encoded images??
            enrollmentService.processEnrollment(enrollmentForm, null, null);
        }
        // TODO: return type
        return new ResponseEntity<>("File is uploaded successfully", HttpStatus.OK);
    }
}
