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
import org.cga.sctp.data.ResourceService;
import org.cga.sctp.targeting.enrollment.*;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/enrollment")
@Tag(name = "Enrollment", description = "Endpoint for enrollment tasks")
@Validated
public class EnrollmentController extends BaseController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private ResourceService resourceService;

    @PostMapping(value = "/{session-id}/update-households")
    @Operation(description = "Updates household enrollment data, specifically status and school enrollment")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Enrollment session does not exist"),
            @ApiResponse(responseCode = "409", description = "Enrollment session cannot be updated at the moment")
    })
    @IncludeGeneralResponses
    public ResponseEntity<?> updateHouseholdEnrollment(
            @AuthenticatedUserDetails ApiUserDetails user,
            @PathVariable("session-id") Long sessionId,
            @Valid @Validated @RequestBody EnrollmentUpdateForm updateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getModel());
        }

        EnrollmentSession session = enrollmentService.getSessionById(sessionId);

        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        if (session.getStatus() == EnrollmentSessionObject.SessionStatus.review && session.getMobileReview()) {
            enrollmentService.updateEnrollmentHouseholdStatuses(sessionId,
                    user.getUserId(), updateRequest.getHouseholdEnrollment());
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping(value = "/{session-id}/update-recipient-photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Updates a household recipient pictures. The returned response ")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Enrollment session does not exist"),
            @ApiResponse(responseCode = "409", description = "Enrollment session cannot be updated at the moment")
    })
    @IncludeGeneralResponses
    public ResponseEntity<RecipientPictureUpdateStatus> updateRecipientPictures(
            @AuthenticatedUserDetails ApiUserDetails user,
            @PathVariable("session-id") Long sessionId,
            @Valid @ModelAttribute RecipientPictureUpdateRequest request,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            LOG.error("Bad request {}", bindingResult.getModel());
            return ResponseEntity.badRequest().build();
        }

        final EnrollmentSession session = enrollmentService.getSessionById(sessionId);

        // todo at some point, isolate by location

        if (session != null) {
            RecipientPictureUpdateStatus updateStatus;
            if (session.getStatus() == EnrollmentSessionObject.SessionStatus.review && session.getMobileReview()) {
                updateStatus = enrollmentService
                        .updateHouseholdRecipientPictures(sessionId, user.getUserId(), request.getData());
                return ResponseEntity.ok(updateStatus);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        return ResponseEntity.notFound().build();
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

    @GetMapping("/sessions/{session-id}/households")
    @Operation(description = "Returns household information under the given session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = EnrollmentSessionHouseholdResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<EnrollmentSessionHouseholdResponse> getEnrollmentSessionHouseholds(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @Valid @Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @Min(100) @Max(1000) @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
            @PathVariable(value = "session-id") Long sessionId
    ) {
        // TODO Restrict session based on location (permission module)
        Page<HouseholdEnrollmentData> enrollmentData = enrollmentService
                .getHouseholdEnrollmentData(sessionId, page, pageSize);
        return ResponseEntity.ok(new EnrollmentSessionHouseholdResponse(enrollmentData));
    }

    @GetMapping(value = "/get-household-recipient-picture", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @Operation(description = "Returns a household's recipient picture.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recipient picture"),
            @ApiResponse(responseCode = "404", description = "Picture does not exist. Resort to offline")
    })
    @IncludeGeneralResponses
    public ResponseEntity<Resource> getHouseholdRecipientPicture(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "recipient-type") HouseholdRecipientType recipientType,
            @RequestParam(value = "household-id") Long householdId) {

        HouseholdRecipient recipient = enrollmentService.getHouseholdRecipient(householdId);
        if (recipient != null) {
            String name = switch (recipientType) {
                case primary -> recipient.getMainPhoto();
                case secondary -> recipient.getAltPhoto();
            };
            String contentType = switch (recipientType) {
                case primary -> recipient.getMainPhotoType();
                case secondary -> recipient.getAltPhotoType();
            };
            if (StringUtils.hasText(name)) {
                Resource resource = resourceService
                        .getRecipientPhotoResource(householdId, recipientType == HouseholdRecipientType.primary);
                if (resource != null && resource.exists()) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.valueOf(contentType))
                            .body(resource);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    // TODO Add this functionality in the MIS. Remove block when implemented
    /*@PostMapping(value = "/mobile-review-completed")
    @Operation(description = "Marks an enrollment session as having been reviewed on the mobile app.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Session does not exist")
    })
    @IncludeGeneralResponses
    public ResponseEntity<?> markEnrollmentSessionMobileReviewAsDone(
            @AuthenticatedUserDetails ApiUserDetails user,
            @RequestParam(value = "session-id") Long sessionId) {
        EnrollmentSession session = enrollmentService.getSessionById(sessionId);

        // todo at some point, isolate by location

        if (session != null) {
            // update only if previous status was true. Otherwise ignore
            if (session.getStatus() == EnrollmentSessionObject.SessionStatus.review && session.getMobileReview()) {
                publishGeneralEvent("mobile review for session %d marked as done by %s",
                        session.getId(), user.getUserName());
                session.setMobileReview(false);
                enrollmentService.saveSession(session);
            }
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }*/


    @PostMapping("/{session-id}/update-member-details")
    @Operation(description = "Updates household member details")
    @ApiResponses({@ApiResponse(responseCode = "200")})
    @IncludeGeneralResponses
    public ResponseEntity<?> updateHouseholdMemberDetails(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @Valid @RequestBody HouseholdMemberUpdateRequest updateRequest,
            @PathVariable(value = "session-id") Long sessionId
    ) {
        EnrollmentSession session = enrollmentService.getSessionById(sessionId);

        if (session == null || !session.getMobileReview()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        enrollmentService.updateHouseholdMemberDetails(sessionId, updateRequest.getUpdates());

        return ResponseEntity.ok().build();
    }
}
