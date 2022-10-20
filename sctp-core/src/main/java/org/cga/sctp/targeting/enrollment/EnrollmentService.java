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
import org.cga.sctp.data.ResourceService;
import org.cga.sctp.targeting.*;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService extends TransactionalService {

    private static final int PAGE_SIZE = 1_000;

    @Autowired
    private EntityManager entityManager;

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
    private SchoolEnrolledViewRepository schoolEnrolledViewRepository;

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
                                                Gender gender,
                                                LocalDate dob) {
        householdRecipientRepository.addHouseholdRecipient(householdId, mainRecipientId, mainPhoto, altPhoto, firstName, lastName, nationalId, gender, dob);
    }

    public EnrollmentSession getSessionById(Long sessionId) {
        return enrolmentSessionRepository.findById(sessionId).orElse(null);
    }

    public void saveChildrenEnrolledSchool(List<SchoolEnrolled> schoolEnrolled) {
        schoolEnrolledRepository.saveAll(schoolEnrolled);
    }

    public void saveChildrenEnrolledSchool(SchoolEnrollmentForm form) {
        EnrollmentUpdateForm.SchoolEnrollment enrollment = new EnrollmentUpdateForm.SchoolEnrollment();
        enrollment.setActive(form.getStatus());
        enrollment.setSchoolId((int) form.getSchoolId());
        enrollment.setEducationLevel(form.getEducationLevel());
        enrollment.setGradeLevel(form.getGrade());
        enrollment.setHouseholdId(form.getHouseholdId());
        enrollment.setMemberId(form.getIndividualId());

        saveSchoolEnrollment(ZonedDateTime.now(), List.of(enrollment));
    }

    public HouseholdRecipient getHouseholdRecipient(Long householdId) {
        return householdRecipientRepository.findById(householdId).orElse(null);
    }

    public AlternateRecipient getHouseholdAlternateRecipient(Long alternateId) {
        return alternateRecipientRepository.findById(alternateId).orElse(null);
    }

    public AlternateRecipient getAlternateRecipientByHouseholdId(Long householdId) {
        return alternateRecipientRepository.getByHouseholdId(householdId);
    }

    public List<SchoolEnrolled> getSchoolEnrolledByHousehold(Long householdId) {
        return schoolEnrolledRepository.findHouseholdSchoolEnrolled(householdId);
    }

    public List<SchoolEnrolledView> getSchoolEnrolledViewByHousehold(Long householdId) {
        return schoolEnrolledViewRepository.findHouseholdSchoolEnrolledView(householdId);
    }

    /*
    public void setEnrollmentHouseholdEnrolled(Long householdId) {
        enrolmentSessionRepository.updateHouseholdEnrollmentStatus(householdId, CbtStatus.Enrolled.code);
    } */

    public void updateHouseholdEnrollmentStatus(Long sessionId, Long householdId, CbtStatus status){
        enrolmentSessionRepository.updateHouseholdEnrollmentStatus(sessionId, householdId, status.name());
    }

    public String getHouseholdEnrollmentStatus(Long sessionId, Long householdId) {
        return enrolmentSessionRepository.getHouseholdEnrollmentStatus(sessionId, householdId);
    }

    public boolean sessionHasPreEligibleHouseholds(Long enrollmentSessionId) {
        return enrolmentSessionRepository.countHouseholdsInSessionByStatus(enrollmentSessionId, CbtStatus.PreEligible.code) > 0;
    }

    public boolean sessionHasHouseholdsWithPreEligibleOrNotYetEnrolled(Long enrollmentSessionId) {
        return enrolmentSessionRepository.countPreEligibleOrNotEnrolled(enrollmentSessionId) > 0;
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

    public AlternateHouseholdRecipient getHouseholdSecondaryRecipient(Long householdId) {
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

    @Transactional
    public void updateEnrollmentHouseholdStatuses(Long sessionId, Long userId, List<EnrollmentUpdateForm.HouseholdEnrollment> list) {
        ZonedDateTime timestamp = ZonedDateTime.now();
        String householdSqlTemplate = """
                UPDATE household_enrollment SET reviewer_id = :user_id
                , reviewed_at = :timestamp
                , last_modified_by = :user_id
                , reviewer_id = :user_id
                , status = :status
                 WHERE session_id = :session_id AND household_id = :household_id
                ;""";
        String schoolEnrollmentSqlTemplate = """
                INSERT INTO school_enrolled(household_id, individual_id, education_level, grade, school_id, status, created_at)
                 VALUES(:household_id, :individual_id, :education_level, :grade_level, :school_id, :status, :timestamp)
                 ON DUPLICATE KEY
                 UPDATE education_level = :education_level, grade = :grade_level, school_id = :school_id
                 , status = :status, updated_at = :timestamp
                ;""";
        String recipientSqlTemplate = """
                INSERT INTO household_recipient(
                household_id
                , main_recipient
                , alt_recipient
                , main_photo
                , alt_photo
                , alt_other
                , created_at
                , modified_at
                , enrollment_session
                , main_photo_type
                , alt_photo_type)
                 VALUES (
                :household_id
                , :main_recipient
                , :alt_recipient
                , NULL
                , NULL
                , :alt_other
                , :timestamp
                , :timestamp
                , :enrollment_session
                , NULL
                , NULL
                ) ON DUPLICATE KEY
                 UPDATE main_recipient = :main_recipient
                 ,alt_recipient = :alt_recipient
                 ,alt_other = :alt_other
                 ,modified_at = :timestamp
                 ,enrollment_session = :enrollment_session
                 ;
                """;
        for (EnrollmentUpdateForm.HouseholdEnrollment enrollment : list) {
            entityManager.createNativeQuery(householdSqlTemplate)
                    .setParameter("timestamp", timestamp)
                    .setParameter("user_id", userId)
                    .setParameter("session_id", sessionId)
                    .setParameter("household_id", enrollment.getHouseholdId())
                    .setParameter("status", enrollment.getStatus().name())
                    .executeUpdate();


            EnrollmentUpdateForm.HouseholdRecipients recipients = enrollment.getRecipients();

            //
            Long alterRecipientId = null, altOtherId = null;

            // if alternate receiver is not a household member, add/update details
            if (recipients.getAlternateMemberId() == null && recipients.getOtherDetails() != null) {
                StoredProcedureQuery query =
                        entityManager.createStoredProcedureQuery("AddOrUpdateNonHouseholdAlternateRecipient")
                                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(5, LocalDate.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(6, LocalDate.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(8, LocalDate.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(9, ZonedDateTime.class, ParameterMode.IN)
                                .registerStoredProcedureParameter(10, Long.class, ParameterMode.OUT)

                                /*.setParameter("householdId", enrollment.getHouseholdId())
                                .setParameter("firstName", recipients.getOtherDetails().getFirstNane())
                                .setParameter("lastName", recipients.getOtherDetails().getLastName())
                                .setParameter("nationalId", recipients.getOtherDetails().getNationalId())
                                .setParameter("nationalIdIssDate", recipients.getOtherDetails().getIssueDate())
                                .setParameter("nationalIdExpDate", recipients.getOtherDetails().getExpiryDate())
                                .setParameter("_gender", recipients.getOtherDetails().getGender().name())
                                .setParameter("dob", recipients.getOtherDetails().getDateOfBirth())
                                .setParameter("ts", timestamp);*/

                                .setParameter(1, enrollment.getHouseholdId())
                                .setParameter(2, recipients.getOtherDetails().getFirstName())
                                .setParameter(3, recipients.getOtherDetails().getLastName())
                                .setParameter(4, recipients.getOtherDetails().getNationalId())
                                .setParameter(5, recipients.getOtherDetails().getIssueDate())
                                .setParameter(6, recipients.getOtherDetails().getExpiryDate())
                                .setParameter(7, recipients.getOtherDetails().getGender().name())
                                .setParameter(8, recipients.getOtherDetails().getDateOfBirth())
                                .setParameter(9, timestamp);
                query.execute();
                // returns the primary Auto value key
                altOtherId = (Long) query.getOutputParameterValue(10);
            } else {
                alterRecipientId = recipients.getAlternateMemberId();
            }

            if (recipients.getPrimaryMemberId() != null && recipients.getPrimaryMemberId() > 0) {
                entityManager.createNativeQuery(recipientSqlTemplate)
                        .setParameter("household_id", enrollment.getHouseholdId())
                        .setParameter("main_recipient", recipients.getPrimaryMemberId())
                        .setParameter("alt_recipient", alterRecipientId)
                        .setParameter("alt_other", altOtherId)
                        .setParameter("timestamp", timestamp)
                        .setParameter("enrollment_session", sessionId)
                        .executeUpdate();
            }


            /*for (EnrollmentUpdateForm.SchoolEnrollment se : enrollment.getSchoolEnrollment()) {
                entityManager.createNativeQuery(schoolEnrollmentSqlTemplate)
                        .setParameter("household_id", se.getHouseholdId())
                        .setParameter("timestamp", timestamp)
                        .setParameter("individual_id", se.getMemberId())
                        .setParameter("education_level", se.getEducationLevel().name())
                        .setParameter("grade_level", se.getGradeLevel().name())
                        .setParameter("school_id", se.getSchoolId())
                        .setParameter("status", se.getActive())
                        .executeUpdate();
            }*/
            saveSchoolEnrollment(timestamp, enrollment.getSchoolEnrollment());
        }
    }

    private void saveSchoolEnrollment(ZonedDateTime timestamp, List<EnrollmentUpdateForm.SchoolEnrollment> enrollments) {
        if (!enrollments.isEmpty()) {
            String schoolEnrollmentSqlTemplate = """
                    INSERT INTO school_enrolled(household_id, individual_id, education_level, grade, school_id, status, created_at)
                     VALUES(:household_id, :individual_id, :education_level, :grade_level, :school_id, :status, :timestamp)
                     ON DUPLICATE KEY
                     UPDATE education_level = :education_level, grade = :grade_level, school_id = :school_id
                     , status = :status, updated_at = :timestamp
                    ;""";

            for (EnrollmentUpdateForm.SchoolEnrollment se : enrollments) {
                entityManager.createNativeQuery(schoolEnrollmentSqlTemplate)
                        .setParameter("household_id", se.getHouseholdId())
                        .setParameter("timestamp", timestamp)
                        .setParameter("individual_id", se.getMemberId())
                        .setParameter("education_level", se.getEducationLevel().name())
                        .setParameter("grade_level", se.getGradeLevel().name())
                        .setParameter("school_id", se.getSchoolId())
                        .setParameter("status", se.getActive())
                        .executeUpdate();
            }
        }
    }

    public void saveSession(EnrollmentSession session) {
        enrolmentSessionRepository.save(session);
    }

    @Autowired
    private ResourceService resourceService;

    /**
     * Processes the uploaded pictures and returns the status of the uploaded files
     *
     * @param sessionId The session under which the update is being done (may be null)
     * @param userId    User id performing the update.
     * @param data      Picture update and metadata
     */
    public RecipientPictureUpdateStatus updateHouseholdRecipientPictures(Long sessionId, Long userId, List<RecipientPictureUpdateRequest.RecipientInformation> data) {
        final ZonedDateTime timestamp = ZonedDateTime.now();
        final RecipientPictureUpdateStatus status = new RecipientPictureUpdateStatus();

        status.setFailed(0);
        status.setUpdated(0);
        status.setReceived(data.size());
        status.setReceivedAt(timestamp);

        for (final RecipientPictureUpdateRequest.RecipientInformation info : data) {
            RecipientPictureUpdateStatus.UpdateStatus updateStatus
                    = updateRecipientPictures(info, timestamp, sessionId);

            if (updateStatus != null) {
                status.setFailed(status.getFailed() + 1);
                status.getFailedStatuses().add(updateStatus);
            } else {
                status.setUpdated(status.getUpdated() + 1);
            }
        }

        status.setCompletedAt(ZonedDateTime.now());
        return status;
    }

    private RecipientPictureUpdateStatus.UpdateStatus updateRecipientPictures(
            RecipientPictureUpdateRequest.RecipientInformation info, ZonedDateTime timestamp, long sessionId) {
        boolean alternateHasError = false, alternatePresent = false, primaryPresent = false, primaryHasError = false;
        ResourceService.UpdateResult primaryResult = null;
        ResourceService.UpdateResult alternateResult = null;
        RecipientPictureUpdateStatus.UpdateStatus status = null;

        if (info.getPrimaryReceiverPicture() != null && !info.getPrimaryReceiverPicture().isEmpty()) {
            primaryResult = resourceService.storeMainRecipientPhoto(info.getPrimaryReceiverPicture(), info.getHouseholdId());
            primaryPresent = primaryResult.stored();
            primaryHasError = primaryResult.error() != null;
        }

        if (info.getAlternateReceiverPicture() != null && !info.getAlternateReceiverPicture().isEmpty()) {
            alternateResult = resourceService.storeAlternateRecipientPhoto(info.getAlternateReceiverPicture(), info.getHouseholdId());
            alternatePresent = alternateResult.stored();
            alternateHasError = alternateResult.error() != null;
        }

        if (primaryHasError || alternateHasError) {
            status = new RecipientPictureUpdateStatus.UpdateStatus();
            status.setHouseholdId(info.getHouseholdId());

            if (primaryHasError) {
                status.setPrimaryRecipientPictureError(primaryResult.error());
            }

            if (alternateHasError) {
                status.setAlternateRecipientPictureError(alternateResult.error());
            }
        }

        if (status == null) {
            StringBuilder builder = new StringBuilder(
                    """
                            UPDATE household_recipient SET modified_at = :timestamp, enrollment_session = :session_id
                            """
            );

            if (primaryPresent) {
                builder.append(",main_photo = :main_photo, main_photo_type = :main_photo_type");
            }

            if (alternatePresent) {
                builder.append(",alt_photo = :alt_photo, alt_photo_type = :alt_photo_type");
            }

            builder.append(" WHERE household_id = :household_id");

            Query query = entityManager.createNativeQuery(builder.toString());

            query.setParameter("household_id", info.getHouseholdId())
                    .setParameter("timestamp", timestamp);
            if (primaryPresent) {
                query.setParameter("main_photo", primaryResult.name())
                        .setParameter("main_photo_type", primaryResult.type());
            }
            if (alternatePresent) {
                query.setParameter("alt_photo", alternateResult.name())
                        .setParameter("alt_photo_type", alternateResult.type());
            }
            query.setParameter("session_id", sessionId);
            query.executeUpdate();
        }

        return status;
    }

}
