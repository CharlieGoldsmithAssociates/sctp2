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

package org.cga.sctp.targeting.enrollment;

import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService extends TransactionalService {

    private static final int PAGE_SIZE = 1_000;

    @Autowired
    private EnrolmentSessionRepository enrolmentSessionRepository;

    @Autowired
    private EnrollmentSessionViewRepository sessionViewRepository;

    @Autowired
    private HouseholdEnrollmentRepository householdEnrollmentRepository;

    @Autowired
    private HouseholdEnrollmentViewRepository enrollmentViewRepository;

    @Autowired
    private AlternateRecipientRepository alternateRecipientRepository;

    @Autowired
    private HouseholdRecipientRepository householdRecipientRepository;

    @Autowired
    private HouseholdRecipientCandidateRepository recipientCandidateRepository;

    @Autowired
    private SchoolEnrolledRepository schoolEnrolledRepository;

    @Autowired
    private MainHouseholdRecipientRepository mainHouseholdRecipientRepository;

    @Autowired
    private AlternateHouseholdRecipientRepository alternateHouseholdRecipientRepository;

    @Autowired
    private SchoolChildrenCandidateRepository schoolChildrenCandidateRepository;

    @Autowired
    private HouseholdEnrollmentSummaryRepository householdEnrollmentSummaryRepository;

    @Autowired
    private HouseholdEnrollmentDataRepository enrollmentDataRepository;

    @Value("${pictures:beneficiary-images}")
    private String beneficiaryPictureUploadDirectory;

    @Value("classpath:passbook/test.pdf")
    private Resource passbookTemplate;

    public Page<EnrollmentSessionView> getEnrollmentSessions(Pageable pageable) {
        return sessionViewRepository.findAll(pageable);
    }

    public Page<EnrollmentSessionView> getEnrollmentSessionsForMobileReview(
            Long districtCode
            , Long taCode
            , Long clusterCode
            , int page
            , int pageSize) {
        List<EnrollmentSessionView> slice = sessionViewRepository
                .getEnrollmentSessionsForMobileReview(districtCode, taCode, clusterCode, page, Math.max(pageSize, PAGE_SIZE));

        // TODO this is necessary for paging on the android front but can be removed to improve performance
        //  just that the app would have to be changed to use optimistic paging.
        Long totalResults = sessionViewRepository.countEnrollmentSessionsForMobileReview(
                districtCode
                , taCode
                , clusterCode
        );

        return new PageImpl<>(slice, PageRequest.of(page, pageSize), totalResults);
    }

    public Page<HouseholdEnrollmentView> getEnrolledHouseholds(EnrollmentSessionView session, Pageable pageable) {
        return enrollmentViewRepository.getBySessionId(session.getId(), pageable);
    }

    public EnrollmentSessionView getEnrollmentSession(Long sessionId) {
        return sessionViewRepository.findById(sessionId).orElse(null);
    }

    public HouseholdEnrollment findHouseholdEnrollment(Long sessionId, long householdId) {
        return householdEnrollmentRepository.findBySessionIdAndHouseholdId(sessionId, householdId);
    }

    public HouseholdDetails getHouseholdDetails(Long householdId) {
        return householdEnrollmentRepository.getEnrolledHouseholdDetails(householdId);
    }

    public void saveAlternateRecipient(AlternateRecipient alternateRecipient) {
        alternateRecipientRepository.save(alternateRecipient);
    }

    public void saveHouseholdRecipient(HouseholdRecipient householdRecipient) {
        householdRecipientRepository.save(householdRecipient);
    }

    public void saveHouseholdAlternateRecipient(Long householdId,
                                                Long mainRecipientId,
                                                String mainPhoto,
                                                String altPhoto,
                                                String firstName,
                                                String lastName,
                                                String nationalId,
                                                int gender,
                                                LocalDate dob) {
        householdRecipientRepository.addHouseholdRecipient(householdId, mainRecipientId, mainPhoto, altPhoto, firstName, lastName, nationalId, gender, dob);
    }

    public void saveChildrenEnrolledSchool(List<SchoolEnrolled> schoolEnrolled) {
        schoolEnrolledRepository.saveAll(schoolEnrolled);
    }

    public HouseholdRecipient getHouseholdRecipient(Long householdId) {
        return householdRecipientRepository.findById(householdId).orElse(null);
    }

    public AlternateRecipient getHouseholdAlternateRecipient(Long alternateId) {
        return alternateRecipientRepository.findById(alternateId).orElse(null);
    }

    public List<SchoolEnrolled> getSchoolEnrolledByHousehold(Long householdId) {
        return schoolEnrolledRepository.findHouseholdSchoolEnrolled(householdId);
    }

    public void setEnrollmentHouseholdEnrolled(Long householdId) {
        enrolmentSessionRepository.updateHouseholdEnrollmentStatus(householdId, CbtStatus.Enrolled.code);
    }

    public void updateHouseholdEnrollmentStatus(Long householdId, CbtStatus status) {
        enrolmentSessionRepository.updateHouseholdEnrollmentStatus(householdId, status.code);
    }

    public boolean sessionHasPreEligibleHouseholds(Long enrollmentSessionId) {
        return enrolmentSessionRepository.countHouseholdsInSessionByStatus(enrollmentSessionId, CbtStatus.PreEligible.code) > 0;
    }

    public boolean sessionHasHouseholdsWithPreEligibleOrNotYetEnrolled(Long enrollmentSessionId) {
        return enrolmentSessionRepository.countPreEligibleOrNotEnrolled(enrollmentSessionId) > 0;
    }


    public void processEnrollment(EnrollmentForm enrollmentForm, MultipartFile file, MultipartFile alternate) throws IOException {
        HouseholdRecipient householdRecipient = new HouseholdRecipient();
        String mainReceiverPhotoName = "main-" + enrollmentForm.getHouseholdId() + ".jpg";
        String altReceiverPhotoName = null;

        //downloadFile(file, mainReceiverPhotoName);
        saveBeneficiaryPicture(file, mainReceiverPhotoName);

        if (enrollmentForm.getHasAlternate() != 0) {
            altReceiverPhotoName = "alt-" + enrollmentForm.getHouseholdId() + ".jpg";
            //downloadFile(alternate, altReceiverPhotoName);

            saveBeneficiaryPicture(alternate, altReceiverPhotoName);

            if (enrollmentForm.getNonHouseholdMember() != 0) {
                this.saveHouseholdAlternateRecipient(enrollmentForm.getHouseholdId(),
                        enrollmentForm.getMainReceiver(),
                        mainReceiverPhotoName,
                        altReceiverPhotoName,
                        enrollmentForm.getAltFirstName(),
                        enrollmentForm.getAltLastName(),
                        enrollmentForm.getAltNationalId(),
                        enrollmentForm.getAltGender(),
                        LocalDate.parse(enrollmentForm.getAltDOB()));
            } else {
                householdRecipient.setHouseholdId(enrollmentForm.getHouseholdId());
                householdRecipient.setMainRecipient(enrollmentForm.getMainReceiver());
                householdRecipient.setAltRecipient(enrollmentForm.getAltReceiver());
                householdRecipient.setCreatedAt(OffsetDateTime.now());
                householdRecipient.setMainPhoto(mainReceiverPhotoName);
                householdRecipient.setAltPhoto(altReceiverPhotoName);
                this.saveHouseholdRecipient(householdRecipient);
            }
        } else {
            householdRecipient.setHouseholdId(enrollmentForm.getHouseholdId());
            householdRecipient.setMainRecipient(enrollmentForm.getMainReceiver());
            householdRecipient.setAltRecipient(enrollmentForm.getAltReceiver());
            householdRecipient.setCreatedAt(OffsetDateTime.now());
            householdRecipient.setMainPhoto(mainReceiverPhotoName);
            householdRecipient.setAltPhoto(altReceiverPhotoName);
            this.saveHouseholdRecipient(householdRecipient);
        }

        List<SchoolEnrolled> schoolEnrolledList = new ArrayList<>();
        List<SchoolEnrollmentForm> schoolEnrollmentForm = enrollmentForm.getSchoolEnrollmentForm();
        if (!schoolEnrollmentForm.isEmpty()) {
            for (SchoolEnrollmentForm sch : schoolEnrollmentForm) {
                schoolEnrolledList.add(new SchoolEnrolled(sch.getHouseholdId(), sch.getIndividualId(), sch.getEducationLevel(), sch.getGrade(), sch.getSchoolId(), sch.getStatus()));
            }
            this.saveChildrenEnrolledSchool(schoolEnrolledList);
        }
        this.setEnrollmentHouseholdEnrolled(enrollmentForm.getHouseholdId());
    }

    private void saveBeneficiaryPicture(MultipartFile file, String fileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(beneficiaryPictureUploadDirectory, fileName))) {
            StreamUtils.copy(file.getInputStream(), fos);
        } catch (IOException e) {
            LOG.error("Failure saving beneficiary image", e);
            throw e;
        }
    }

    public List<SchoolChildrenCandidate> getSchoolGoingChildrenCandidates(Long household) {
        return schoolChildrenCandidateRepository.findByHouseholdId(household);
    }

    /**
     * Returns a household's passbook
     *
     * @param enrollment enrollment session id
     * @param household  household id
     * @return {@link  Optional} containing the pfd resource or null
     */
    public Optional<Resource> getHouseholdPassbookResource(Long enrollment, Long household) {
        //HouseholdPassbook
        /*try (OutputStream os = new FileOutputStream(outputPdf)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(outputPdf);
            builder.toStream(os);
            builder.withW3cDocument(new W3CDom().fromJsoup(doc), "/");
            builder.run();
        }*/
        return Optional.of(passbookTemplate);
    }

    public HouseholdEnrollmentSummary getHouseholdEnrollmentSummary(Long enrollment, Long household) {
        return householdEnrollmentSummaryRepository.getBySessionIdAndHouseholdId(enrollment, household);
    }

    public MainHouseholdRecipient getHouseholdPrimaryRecipient(Long householdId) {
        return mainHouseholdRecipientRepository.getByHouseholdId(householdId);
    }

    public MainHouseholdRecipient getHouseholdSecondaryRecipient(Long householdId) {
        return alternateHouseholdRecipientRepository.getByHouseholdId(householdId);
    }

    public List<HouseholdRecipientCandidate> getHouseholdRecipientCandidates(Long household) {
        return recipientCandidateRepository.getByHouseholdId(household);
    }

    public void saveEnrollment(HouseholdEnrollment enrollment) {
        householdEnrollmentRepository.save(enrollment);
    }

    public Page<HouseholdEnrollmentData> getHouseholdEnrollmentData(Long sessionId, int page, int pageSize) {
        return enrollmentDataRepository.findBySessionId(sessionId,
                PageRequest.of(page, Math.max(pageSize, PAGE_SIZE)));
    }
}
