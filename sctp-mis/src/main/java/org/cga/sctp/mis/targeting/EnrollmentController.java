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

package org.cga.sctp.mis.targeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.beneficiaries.Individual;
import org.cga.sctp.data.ResourceService;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.mis.file_upload.FileUploadService;
import org.cga.sctp.schools.SchoolService;
import org.cga.sctp.schools.SchoolsView;
import org.cga.sctp.targeting.*;
import org.cga.sctp.targeting.enrollment.SchoolEnrollmentForm;
import org.cga.sctp.targeting.enrollment.*;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.cga.sctp.targeting.importation.parameters.GradeLevel;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/targeting/enrolment")
public class EnrollmentController extends SecuredBaseController {
    private static final HouseholdEnrollmentSummary EMPTY_SUMMARY = new HouseholdEnrollmentSummary();
    private static final HouseholdRecipientSummary EMPTY_RECIPIENT = new HouseholdRecipientSummary() {
    };

    enum _RecipientType {
        primary,
        secondary
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private TargetingConfig config;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView index(Pageable pageable) {
        return view("targeting/enrolment/sessions",
                "sessions", enrollmentService.getEnrollmentSessions(pageable)
        );
    }

    @GetMapping("/households")
    @AdminAndStandardAccessOnly
    public ModelAndView households(@RequestParam("session") Long sessionId, RedirectAttributes attributes, Pageable pageable) {
        EnrollmentSessionView sessionView = enrollmentService.getEnrollmentSession(sessionId);
        if (sessionView == null) {
            setDangerFlashMessage("Enrollment session not found.", attributes);
            return redirect("/targeting/enrolment");
        }
        Page<HouseholdEnrollmentView> households = enrollmentService.getEnrolledHouseholds(sessionView, pageable);
        return view("targeting/enrolment/households")
                .addObject("sessionInfo", sessionView)
                .addObject("households", households);
    }


    @GetMapping("/details")
    @AdminAndStandardAccessOnly
    public ModelAndView details(@RequestParam("id") Long householdId,
                                @RequestParam("session") Long sessionId, RedirectAttributes attributes,
                                @ModelAttribute("enrollmentForm") EnrollmentForm enrollmentForm) {

        HouseholdEnrollment householdEnrollment = enrollmentService.findHouseholdEnrollment(sessionId, householdId);

        if (householdEnrollment == null) {
            setDangerFlashMessage("Enrollment session not found.", attributes);
            return redirect("/targeting/enrolment");
        }

        /*HouseholdDetails householdDetails = enrollmentService.getHouseholdDetails(householdId);
        if (householdDetails == null) {
            setDangerFlashMessage("Enrollment household not found.", attributes);
            return redirect("/targeting/enrolment/households?session=" + sessionId);
        }*/
/*
        List<SchoolsView> schools = schoolService.getSchools();
        List<Individual> children = beneficiaryService.findSchoolChildren(householdId);
        List<Individual> individuals = beneficiaryService.getEligibleRecipients(householdId);

        String returnUrl = "households?session=" + sessionId;*/

        return view("targeting/enrolment/details")
                .addObject("enrollment", householdEnrollment)
                .addObject("sessionId", sessionId)
                .addObject("householdId", householdId)
/*                .addObject("details", householdDetails)*/
                .addObject("gender", Gender.VALUES)
/*                .addObject("children", children)*/
                .addObject("educationLevel", EducationLevel.VALUES)
                .addObject("gradeLevel", GradeLevel.VALUES)
/*                .addObject("returnUrl", returnUrl)
                .addObject("schools", schools)
                .addObject("individuals", individuals)*/;
    }

    @GetMapping("/edit")
    @AdminAccessOnly
    public ModelAndView edit(@RequestParam("id") Long householdId,
                             @RequestParam("session") Long sessionId, RedirectAttributes attributes,
                             @ModelAttribute("enrollmentForm") EnrollmentForm enrollmentForm) {

        HouseholdEnrollment enrollmentHousehold = enrollmentService.findHouseholdEnrollment(sessionId, householdId);

        if (enrollmentHousehold == null) {
            setDangerFlashMessage("Enrollment session not found.", attributes);
            return redirect("/targeting/enrolment");
        }

        HouseholdDetails householdDetails = enrollmentService.getHouseholdDetails(householdId);

        if (householdDetails == null) {
            setDangerFlashMessage("Household not found.", attributes);
            return redirect("/targeting/enrolment/households?session=" + sessionId);
        }

        List<Individual> children = beneficiaryService.findSchoolChildren(householdId);
        List<Individual> individuals = beneficiaryService.getHouseholdMembers(householdId);
        List<SchoolEnrolled> schoolEnrolled = enrollmentService.getSchoolEnrolledByHousehold(householdId);

        List<SchoolsView> schools = schoolService.getSchools();
        String returnUrl = "households?session=" + sessionId;

        HouseholdRecipient householdRecipient = enrollmentService.getHouseholdRecipient(householdId);

        if (householdRecipient.getAltOther() != 0) {
            AlternateRecipient alternateRecipient = enrollmentService.getHouseholdAlternateRecipient(householdRecipient.getAltOther());
            enrollmentForm.setAltGender(alternateRecipient.getGender());
            enrollmentForm.setAltDOB(alternateRecipient.getDateOfBirth().toString());
            enrollmentForm.setAltFirstName(alternateRecipient.getFirstName());
            enrollmentForm.setAltLastName(alternateRecipient.getLastName());
            enrollmentForm.setAltNationalId(alternateRecipient.getNationalId());
        }

        enrollmentForm.setMainReceiver(householdRecipient.getMainRecipient());
        enrollmentForm.setAltReceiver(householdRecipient.getAltRecipient());
        enrollmentForm.setHasAlternate((householdRecipient.getAltOther() != 0 || householdRecipient.getAltRecipient() != 0) ? 1 : 0);
        enrollmentForm.setNonHouseholdMember((householdRecipient.getAltOther() != 0) ? 1 : 0);

        return view("targeting/enrolment/details")
                .addObject("details", householdDetails)
                .addObject("gender", Gender.VALUES)
                .addObject("children", children)
                .addObject("educationLevel", EducationLevel.VALUES)
                .addObject("gradeLevel", GradeLevel.VALUES)
                .addObject("returnUrl", returnUrl)
                .addObject("parent", 1)
                .addObject("schools", schools)
                .addObject("schoolEnrolled", schoolEnrolled)
                .addObject("individuals", individuals);
    }

    @GetMapping(value = "/passbook", produces = "application/pdf")
    @AdminAndStandardAccessOnly
    ResponseEntity<?> getHouseholdPassbook(@RequestParam("household") Long household, @RequestParam("enrollment") Long enrollment) {
        Resource resource = enrollmentService.getHouseholdPassbookResource(enrollment, household).orElse(null);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .body(resource);
    }

    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<HouseholdEnrollmentSummary> getHouseholdEnrollmentSummary(@RequestParam("household") Long household, @RequestParam("enrollment") Long enrollment) {
        HouseholdEnrollmentSummary summary = enrollmentService.getHouseholdEnrollmentSummary(enrollment, household);
        return ResponseEntity.ok()
                .body(Objects.requireNonNullElse(summary, EMPTY_SUMMARY));
    }

    private ResponseEntity<HouseholdRecipientSummary> getHouseholdRecipient(Long household, boolean primary) {
        HouseholdRecipientSummary recipient = primary
                ? enrollmentService.getHouseholdPrimaryRecipient(household)
                : enrollmentService.getHouseholdSecondaryRecipient(household);
        return ResponseEntity.ok(Objects.requireNonNullElse(recipient, EMPTY_RECIPIENT));
    }

    @GetMapping(value = "/primary-recipient", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<HouseholdRecipientSummary> getHouseholdPrimaryRecipient(@RequestParam("household") Long household) {
        return getHouseholdRecipient(household, true);
    }

    @GetMapping(value = "/secondary-recipient", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<HouseholdRecipientSummary> getHouseholdSecondaryRecipient(@RequestParam("household") Long household) {
        return getHouseholdRecipient(household, false);
    }

    @GetMapping(value = "/recipient-photo")
    @AdminAndStandardAccessOnly
    ResponseEntity<Resource> getHouseholdRecipientPhoto(@RequestParam(value = "household") Long household, @RequestParam HouseholdRecipientType type) {
        HouseholdRecipient recipient = enrollmentService.getHouseholdRecipient(household);
        if (recipient != null) {
            String name = switch (type) {
                case primary -> recipient.getMainPhoto();
                case secondary -> recipient.getAltPhoto();
            };
            String contentType = switch (type) {
                case primary -> recipient.getMainPhotoType();
                case secondary -> recipient.getAltPhotoType();
            };
            if (StringUtils.hasText(name)) {
                Resource resource = fileUploadService.getResourceService()
                        .getRecipientPhotoResource(household, type == HouseholdRecipientType.primary);
                if (resource != null && resource.exists()) {
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.valueOf(contentType))
                            .body(resource);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/static/img/user-svg.svg"))
                .build();
    }

    @GetMapping(value = "/recipient-candidates", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<Map<String, Object>> getHouseholdCandidates(@RequestParam(value = "household") Long household) {
        List<HouseholdRecipientCandidate> candidates = enrollmentService.getHouseholdRecipientCandidates(household);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("candidates", candidates);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/update-recipient")
    public ResponseEntity<?> updateRecipient(
            @RequestParam HouseholdRecipientType type,
            @Valid @ModelAttribute UpdateHouseholdRecipientForm form,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Long householdId = form.getHousehold();
        HouseholdEnrollment enrollment = enrollmentService
                .findHouseholdEnrollment(form.getSession(), householdId);
        // TODO send the whole form, extract what data you want
        if (enrollment == null) {
            return ResponseEntity.notFound().build();
        }

        ZonedDateTime timestamp = ZonedDateTime.now();
        HouseholdRecipient recipient = enrollmentService.getHouseholdRecipient(householdId);

        if (recipient == null) {
            recipient = new HouseholdRecipient();
            recipient.setCreatedAt(timestamp);
            recipient.setHouseholdId(householdId);
            recipient.setEnrollmentSession(form.getSession());
        }

        recipient.setModifiedAt(timestamp);
        enrollment.setUpdatedAt(timestamp);

        MultipartFile photo = form.getPhoto();

        if (!fileUploadService.getResourceService().isAcceptedImageFile(photo)) {
            return ResponseEntity.badRequest().build();
        }

        ResourceService.UpdateResult updateResult = switch (type) {
            case primary -> fileUploadService.getResourceService().storeMainRecipientPhoto(photo, householdId);
            case secondary ->
                    fileUploadService.getResourceService().storeAlternateRecipientPhoto(photo, householdId);
        };

        if (!updateResult.stored()) {
            return ResponseEntity.badRequest().body("Cannot upload the file");
        }

       switch (type) {
            case primary -> {
                // verify household member existence
                if (!beneficiaryService.householdMemberExists(householdId, form.getId())) {
                    return ResponseEntity.notFound().build();
                }
                recipient.setMainRecipient(form.getId());
                recipient.setMainPhoto(updateResult.name());
                recipient.setMainPhotoType(updateResult.type());
            }
            case secondary -> {
                recipient.setAltPhoto(updateResult.name());
                recipient.setAltPhotoType(updateResult.type());
                AlternateRecipient altRecipient = enrollmentService.getAlternateRecipientByHouseholdId(householdId);
                if(altRecipient == null){
                    altRecipient = new AlternateRecipient();
                }
                // check if recipient is member or other
                if (form.getAltType().equals(AlternateRecipientType.member)) {
                    Individual individual = beneficiaryService.getIndividual(form.getId());
                    if (individual == null){
                        return ResponseEntity.notFound().build();
                    }
                    recipient.setAltRecipient(individual.getId());
                    altRecipient.setGender(individual.getGender());
                    altRecipient.setLastName(individual.getLastName());
                    altRecipient.setFirstName(individual.getFirstName());
                    altRecipient.setNationalId(individual.getIndividualId());
                    altRecipient.setHouseholdId(individual.getHouseholdId());
                    altRecipient.setDateOfBirth(individual.getDateOfBirth());
                    altRecipient.setIdIssueDate(individual.getIdIssueDate());
                    altRecipient.setIdExpiryDate(individual.getIdExpiryDate());
                } else {  // type is other
                    altRecipient.setGender(form.getGender());
                    altRecipient.setLastName(form.getLastName());
                    altRecipient.setFirstName(form.getFirstName());
                    altRecipient.setNationalId(form.getNationalId());
                    altRecipient.setHouseholdId(form.getHousehold());
                    altRecipient.setDateOfBirth(form.getDateOfBirth());
                    altRecipient.setIdExpiryDate(form.getIdExpiryDate()); // TODO but must be optional
                    altRecipient.setIdIssueDate(form.getIdIssueDate());  // TODO but must be optional
                    // TODO All data must be validated if present,
                }

                enrollmentService.saveAlternateRecipient(altRecipient);
                recipient.setAltRecipient(altRecipient.getId());

            }
        }

        enrollmentService.saveHouseholdRecipient(recipient);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/school-children", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<Map<String, Object>> getSchoolChildren(@RequestParam(value = "household") Long householdId) {
        List<Individual> children = beneficiaryService.findSchoolChildren(householdId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("children", children);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping(value = "/schools", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<Map<String, Object>> getSchools() {
        List<SchoolsView> schools = schoolService.getSchools();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("schools", schools);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping(value = "/education-levels", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<Map<String, Object>> getEducationLevels() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("educationLevels", EducationLevel.VALUES);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping(value = "/grade-levels", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<Map<String, Object>> getGradeLevels() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("gradeLevels", GradeLevel.VALUES);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/update-school")
    public ResponseEntity<?> updateSchool(
            @Valid @ModelAttribute SchoolEnrollmentForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        HouseholdEnrollment enrollment = enrollmentService
                .findHouseholdEnrollment(form.getSessionId(), form.getHouseholdId());

        if (enrollment == null) {
            return ResponseEntity.notFound().build();
        }
        if (schoolService.findById(form.getSchoolId()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        enrollmentService.saveChildrenEnrolledSchool(form);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/schools-enrolled", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<Map<String, List<SchoolEnrolledView>>> getHouseholdSchoolEnrollments(@RequestParam(value = "household") Long householdId) {
        List<SchoolEnrolledView> schools = enrollmentService.getSchoolEnrolledViewByHousehold(householdId);
        Map<String, List<SchoolEnrolledView>> response = new LinkedHashMap<>();
        response.put("schools", schools);
        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/household-status")
    public ResponseEntity<?> updateHouseholdStatus(
            @Valid @ModelAttribute UpdateHouseholdStatusForm form,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Long householdId, sessionId;
        CbtStatus householdStatus;
        householdId = form.getHousehold();
        sessionId = form.getSession();
        householdStatus = form.getStatus();
        // check if household and session exists
        HouseholdEnrollment enrollment = enrollmentService
                .findHouseholdEnrollment(sessionId, householdId);
        if (enrollment == null) {
            return ResponseEntity.notFound().build();
        }

        if (householdStatus.name().equals(CbtStatus.Enrolled.name())){
            // check if main recipient is set
            if (enrollmentService.getHouseholdPrimaryRecipient(householdId) == null){
                return ResponseEntity.badRequest().body("Please set the main recipient first");
            }
        }
        if (!householdStatus.name().equals(CbtStatus.Enrolled.name()) && !householdStatus.name().equals(CbtStatus.Selected.name())){
            LOG.error("Status should be either selected or enrolled");
            return ResponseEntity.badRequest().build();
        }
        enrollmentService.updateHouseholdEnrollmentStatus(sessionId, householdId, householdStatus);

        return ResponseEntity.ok().build();
     }

    @GetMapping(value = "/household-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAndStandardAccessOnly
    ResponseEntity<Map<String, Object>> getHouseholdEnrollmentStatus(@RequestParam("session") Long sessionId, @RequestParam("household") Long householdId) {
        Map<String, Object> response = new LinkedHashMap<>();
        String status = enrollmentService.getHouseholdEnrollmentStatus(sessionId, householdId);
        response.put("householdStatus", status);
        return ResponseEntity
                .ok()
                .body(response);
    }

}
