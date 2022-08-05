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

package org.cga.sctp.api.enrollment;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cga.sctp.api.core.BaseController;
import org.cga.sctp.api.core.IncludeGeneralResponses;
import org.cga.sctp.api.user.ApiUserDetails;
import org.cga.sctp.api.utils.LangUtils;
import org.cga.sctp.targeting.enrollment.EnrollmentForm;
import org.cga.sctp.targeting.enrollment.EnrollmentService;
import org.cga.sctp.targeting.enrollment.EnrollmentSessionView;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;

@RestController
@RequestMapping("/enrollment")
@Tag(name = "Enrollment", description = "Endpoint for enrollment tasks")
public class EnrollmentController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EnrollmentService enrollmentService;

    /*@GetMapping
    public ResponseEntity<GetEnrollmentSessionResponse> getEnrollmentSessions(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "traditional-authority-code", required = false) Long taCode,
            @RequestParam(value = "village-cluster-code", required = false) Long villageCluster,
            @RequestParam(value = "zone-code", required = false) Long zone,
            @RequestParam(value = "village-code", required = false) Long village,
            @Valid @Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @Min(100) @Max(1000) @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize) {


        enrollmentService.get
    }*/

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

        for (EnrollmentForm enrollmentForm : request.getItems()) {
            // TODO: How do we get the image files? Do we request Base64 encoded images??
            enrollmentService.processEnrollment(enrollmentForm, null, null);
        }
        // TODO: return type
        return new ResponseEntity<>("File is uploaded successfully", HttpStatus.OK);
    }

    @GetMapping("/sessions")
    @Operation(description = "Returns enrollment sessions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = EnrollmentSessionListResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<EnrollmentSessionListResponse> getEnrollmentSessions(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @Valid @Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @Min(100) @Max(1000) @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
            @RequestParam(value = "traditional-authority-code", required = false) Long taCode,
            @RequestParam(value = "village-cluster-code", required = false) Long villageCluster
    ) {
        Page<EnrollmentSessionView> sessions = enrollmentService
                .getEnrollmentSessionsForMobileReview(
                        apiUserDetails.getAccessTokenClaims().getDistrictCode().longValue(),
                        LangUtils.nullIfZeroOrLess(taCode),
                        LangUtils.nullIfZeroOrLess(villageCluster),
                        page,
                        pageSize
                );
        return ResponseEntity.ok(new EnrollmentSessionListResponse(sessions));
    }
}
