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

package org.cga.sctp.targeting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.criteria.*;
import org.cga.sctp.targeting.enrollment.EnrolmentSessionRepository;
import org.cga.sctp.utils.CollectionUtils;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TargetingService extends TransactionalService {
    private static final int PAGE_SIZE = 1000;

    @Autowired
    private CbtRankingRepository cbtRankingRepository;

    @Autowired
    private TargetingSessionRepository targetingSessionRepository;

    /*@Autowired
    private TargetingSessionViewRepository viewRepository;*/

    @Autowired
    private CriterionRepository criterionRepository;

    @Autowired
    private CriterionViewRepository criterionViewRepository;

    @Autowired
    private CriteriaFilterTemplateRepository filterTemplateRepository;

    @Autowired
    private CriteriaFilterRepository criteriaFilterRepository;

    @Autowired
    private CriteriaFilterViewRepository criteriaFilterViewRepository;

    @Autowired
    private FilterTemplateListOptionRepository filterTemplateListOptionRepository;

    @Autowired
    private EligibilityVerificationSessionRepository verificationSessionRepository;

    @Autowired
    private EligibilityVerificationSessionViewRepository verificationSessionViewRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EnrolmentSessionRepository enrolmentRepository;

    @Autowired
    private EligibleHouseholdDetailsRepository eligibleHouseholdDetailsRepository;

    @Autowired
    private TargetingResultRepository targetingResultRepository;

    @Autowired
    private TargetingSessionViewRepository targetingSessionViewRepository;

    @Autowired
    private TargetedHouseholdSummaryRepository targetedHouseholdSummaryRepository;

    public void saveTargetingSession(TargetingSession targetingSession) {
        targetingSessionRepository.save(targetingSession);
    }

    public void performCommunityBasedTargetingRanking(TargetingSession session) {
        if (session.isOpen()) {
            targetingSessionRepository.runCommunityBasedTargetingRanking(session.getId());
        }
    }

    public Page<TargetingSessionView> targetingSessionViewList(Pageable pageable) {
        return targetingSessionViewRepository.findAll(pageable);
    }

    public TargetingSession findSessionById(Long sessionId) {
        return targetingSessionRepository.findById(sessionId).orElse(null);
    }

    public Slice<CbtRankingResult> getCbtRanking(TargetingSessionView session, Pageable pageable) {
        return cbtRankingRepository.findByCbtSessionId(session.getId(), pageable);
    }

    public TargetingSessionView findTargetingSessionViewById(Long districtCode, Long sessionId) {
        return targetingSessionViewRepository.findByIdAndDistrictCode(sessionId, districtCode);
    }

    public TargetingSession findTargetingSessionById(Long districtCode, Long sessionId) {
        return targetingSessionRepository.findByIdAndDistrictCode(sessionId, districtCode);
    }

    public TargetingSessionView findTargetingSessionViewById(Long sessionId) {
        return targetingSessionViewRepository.findById(sessionId).orElse(null);
    }

    public TargetingSession findTargetingSessionById(Long sessionId) {
        return targetingSessionRepository.findById(sessionId).orElse(null);
    }

    public void closeTargetingSession(TargetingSessionView session, Long userId) {
        targetingSessionRepository.closeSession(session.getId(), userId, LocalDateTime.now(),
                TargetingSessionBase.SessionStatus.Closed.name());

        enrolmentRepository.createEnrollmentSessionFromCbtOrPev(session.getId(), (long) 0, userId);
    }

    public void saveTargetingCriterion(Criterion criterion) {
        criterionRepository.save(criterion);
    }

    public void deleteTargetingCriterion(Criterion criterion) {
        criterionRepository.delete(criterion);
    }

    public List<CriterionView> getTargetingCriteria() {
        return criterionViewRepository.findAll();
    }

    public List<CriterionView> getActiveTargetingCriteriaViews() {
        return criterionViewRepository.findByActive(true);
    }

    public List<Criterion> getActiveTargetingCriteria() {
        return criterionRepository.findByActive(true);
    }

    public Criterion findCriterionById(Long id) {
        return criterionRepository.findById(id).orElse(null);
    }

    public List<CriteriaFilter> getFiltersByCriterionId(Long criterionId) {
        return criteriaFilterRepository.findByCriterionId(criterionId);
    }

    public List<CriteriaFilterTemplate> getFilterTemplates() {
        return filterTemplateRepository.findAll();
    }

    public List<CriteriaFilterTemplate> getIndividualFilterTemplates() {
        return filterTemplateRepository.getByTableName("individuals");
    }

    public List<CriteriaFilterTemplate> getHouseholdFilterTemplates() {
        return filterTemplateRepository.getByTableName("households");
    }

    public CriteriaFilterTemplate findFilterTemplateById(Long templateId) {
        return filterTemplateRepository.findById(templateId).orElse(null);
    }

    public void saveCriteriaFilter(CriteriaFilter filter) {
        criteriaFilterRepository.save(filter);
    }

    public List<FilterTemplateListOption> getFilterTemplateListOptions(Long templateId) {
        return filterTemplateListOptionRepository.findByTemplateId(templateId);
    }

    public List<CriteriaFilterView> getFilterViewsByCriterionId(Long criterionId) {
        return criteriaFilterViewRepository.findByCriterionId(criterionId);
    }

    public CriteriaFilter findCriteriaFilterById(Long filter, Long criterionId) {
        return criteriaFilterRepository.findByIdAndCriterionId(filter, criterionId);
    }

    public void deleteCriteriaFilter(CriteriaFilter filter) {
        criteriaFilterRepository.delete(filter);
    }


    private String placeholder(CriteriaFilterInfo info) {
        return format("%s_%s_%s%d",
                info.getConjunction().name().toLowerCase(),
                info.getTableName(), info.getColumnName(), info.getId()
        );
    }

    public void compileFilterQuery(Criterion criterion) {
        List<CriteriaFilterInfo> criteriaFilterInfoList =
                criteriaFilterRepository.getFilterValuesForCriterion(criterion.getId());
        if (criteriaFilterInfoList.isEmpty()) {
            return;
        }

        StringBuilder builder;
        List<CriteriaFilterInfo> ands;
        List<CriteriaFilterInfo> ors;

        boolean hasHouseholdFilters;
        boolean hasIndividualFilters;

        ands = criteriaFilterInfoList.stream()
                .filter(cfi -> cfi.getConjunction() == CriteriaFilterObject.Conjunction.And || cfi.getConjunction() == CriteriaFilterObject.Conjunction.None)
                .collect(Collectors.toList());

        ors = criteriaFilterInfoList.stream()
                .filter(cfi -> cfi.getConjunction() == CriteriaFilterObject.Conjunction.Or)
                .collect(Collectors.toList());

        hasIndividualFilters = criteriaFilterInfoList.stream()
                .anyMatch(cfi -> cfi.getTableName().equalsIgnoreCase("individuals"));

        hasHouseholdFilters = criteriaFilterInfoList.stream()
                .anyMatch(cfi -> cfi.getTableName().equalsIgnoreCase("households"));

        StringBuilder clauseBuilder = new StringBuilder();

        if (!ands.isEmpty()) {
            clauseBuilder.append('(');
            boolean first = true;
            for (CriteriaFilterInfo info : ands) {
                if (!first) {
                    clauseBuilder.append(" AND ");
                }
                first = false;
                clauseBuilder.append(info.getTableName())
                        .append(".")
                        .append(info.getColumnName())
                        .append(" = :").append(placeholder(info));
            }
            clauseBuilder.append(')');
        }

        if (!ors.isEmpty()) {
            if (!clauseBuilder.isEmpty()) {
                clauseBuilder.append(" OR ");
            }
            clauseBuilder.append('(');
            boolean first = true;
            for (CriteriaFilterInfo info : ors) {
                if (!first) {
                    clauseBuilder.append(" OR ");
                }
                first = false;
                clauseBuilder.append(info.getTableName())
                        .append(".")
                        .append(info.getColumnName())
                        .append(" = :").append(placeholder(info));
            }
            clauseBuilder.append(')');
        }

        // Compile the query only. The actual run and parameter binding will be done later.

        builder = new StringBuilder("SELECT households.household_id, households.location_code, households.ta_code,")
                .append("households.zone_code, households.cluster_code FROM households");

        if (hasHouseholdFilters) {
            if (hasIndividualFilters) {
                builder.append(" JOIN individuals ON individuals.household_id = households.household_id");
            }
        } else {
            builder.append(" JOIN individuals ON individuals.household_id = households.household_id");
        }

        builder.append(" WHERE (").append(clauseBuilder).append(") GROUP BY household_id");

        String query = builder.toString();

        criterion.setCompiledAt(LocalDateTime.now());
        criterion.setCompiledQuery(query);

        saveTargetingCriterion(criterion);
    }

    public long getCriterionFilterCount(CriterionObject criterion) {
        return criteriaFilterRepository.countByCriterionId(criterion.getId());
    }

    public EligibilityVerificationSessionView findVerificationViewById(Long id) {
        return verificationSessionViewRepository.findById(id).orElse(null);
    }

    public void saveEligibilityVerificationSession(EligibilityVerificationSession session) {
        verificationSessionRepository.save(session);
    }

    public void addEligibilityVerificationSession(EligibilityVerificationSession session, Criterion criterion, Long userId) {
        if (session instanceof HibernateProxy) {
            return;
        }
        verificationSessionRepository.save(session);
        runEligibilityVerification(session, criterion, userId);
        verificationSessionRepository.calculateHouseholdCount(session.getId());
    }

    /**
     * Runs the eligibility verification. This method must only be called when creating a {@link EligibilityVerificationSession}
     *
     * @param session   A managed entity recently persisted.
     * @param criterion Criterion from which filters will be used to evaluate households
     * @param userId    The user who initiated this run.
     */
    private void runEligibilityVerification(EligibilityVerificationSession session, Criterion criterion, Long userId) {

        List<CriteriaFilterInfo> criteriaFilterInfoList = criteriaFilterRepository
                .getFilterValuesForCriterion(session.getCriterionId());

        StringBuilder builder = new StringBuilder(" INSERT INTO eligible_households (session_id, household_id, created_at, run_by, selection_status)")
                .append(" WITH _insert_ AS (").append(criterion.getCompiledQuery()).append(")")
                .append(" SELECT :sessionId, household_id, :createdAt, :runBy, :selectionStatus")
                .append(" FROM _insert_")
                .append(" WHERE location_code = :districtCode AND ta_code = :taCode")
                .append(" AND FIND_IN_SET(cluster_code, :clusterCodes)");

        String sql = builder.toString();

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("runBy", userId)
                .setParameter("selectionStatus", CbtStatus.PreEligible.name())
                .setParameter("taCode", session.getTaCode())
                .setParameter("sessionId", session.getId())
                .setParameter("createdAt", LocalDateTime.now())
                .setParameter("districtCode", session.getDistrictCode())
                .setParameter("clusterCodes", CollectionUtils.join(session.getClusters()));

        for (CriteriaFilterInfo info : criteriaFilterInfoList) {
            query.setParameter(placeholder(info), info.getFilterValue());
        }

        query.executeUpdate();
    }

    public Page<EligibilityVerificationSessionView> getVerificationSessionViews(Pageable pageable) {
        return verificationSessionViewRepository.findAll(pageable);
    }

    public Criterion getActiveTargetingCriterionById(Long criterion) {
        return criterionRepository.findById(criterion).orElse(null);
    }

    public EligibilityVerificationSession getVerificationSessionById(Long id) {
        return verificationSessionRepository.findById(id).orElse(null);
    }

    public void closeVerificationSession(EligibilityVerificationSession session,
                                         VerificationSessionDestination destination, Long userId) {
        session.setStatus(EligibilityVerificationSessionBase.Status.Closed);
        verificationSessionRepository.save(session);

        // Send to next module if the session did have households that matched
        if (session.getHouseholds() > 0) {
            switch (destination) {
                case enrolment -> enrolmentRepository.createEnrollmentSessionFromCbtOrPev(0L, session.getId(), userId);
                case targeting -> {
                    // 1. Create a targeting session
                    TargetingSession targetingSession = new TargetingSession();
                    targetingSession.setCreatedBy(userId);
                    targetingSession.setTaCode(session.getTaCode());
                    targetingSession.setPevSession(session.getId());
                    targetingSession.setCreatedAt(LocalDateTime.now());
                    targetingSession.setClusters(session.getClusters());
                    targetingSession.setProgramId(session.getProgramId());
                    targetingSession.setDistrictCode(session.getDistrictCode());
                    targetingSession.setStatus(TargetingSessionBase.SessionStatus.Review);
                    targetingSession.setMeetingPhase(TargetingSessionBase.MeetingPhase.second_community_meeting);

                    saveTargetingSession(targetingSession);

                    // 2. Run CBT on the households selected
                    targetingSessionRepository.runCommunityBasedTargetingRankingOnEligibleHouseholds(targetingSession.getId(), session.getId());
                }
            }
        } else {
            LOG.warn("Pre-eligibility session with id {} did not have any households to be sent to either enrolment or targeting",
                    session.getId());
        }
    }

    public CriterionView findCriterionViewById(Long id) {
        return criterionViewRepository.findById(id).orElse(null);
    }

    public Long getCriterionUsageCount(Criterion criterion) {
        return criterionRepository.getUsageCount(criterion.getId());
    }

    public Page<EligibleHousehold> getEligibleHouseholds(EligibilityVerificationSessionBase session, Pageable pageable) {
        List<EligibleHousehold> householdList = verificationSessionRepository
                .getEligibleHouseholds(session.getId(), pageable.getPageNumber(), pageable.getPageSize());
        long total = verificationSessionRepository.countEligibleHouseholds(session.getId());
        return new PageImpl<>(householdList, pageable, total);
    }

    public Page<EligibleHouseholdDetails> getEligibleHouseholdsDetails(Long sessionId, int page) {
        return eligibleHouseholdDetailsRepository.getBySessionId(
                sessionId,
                PageRequest.of(page, PAGE_SIZE)
        );
    }

    @Deprecated(forRemoval = true)
    public Page<EligibilityVerificationSessionView> getOpenVerificationSessionsByLocation(
            long districtCode
            , Long taCode
            , Long villageClusterCode
            , Long zoneCode
            , Long villageCode
            , int page) {
        if (isCodeSet(taCode) && isCodeSet(villageClusterCode)) {
            return verificationSessionViewRepository.findByOpenByLocation(
                    EligibilityVerificationSessionBase.Status.Review.name()
                    , districtCode
                    , taCode
                    , villageClusterCode
                    , PageRequest.of(page, PAGE_SIZE)
            );
        }
        if (isCodeSet(taCode) && !isCodeSet(villageClusterCode)) {
            return verificationSessionViewRepository.findByOpenByLocation(
                    EligibilityVerificationSessionBase.Status.Review.name()
                    , districtCode
                    , taCode
                    , PageRequest.of(page, PAGE_SIZE)
            );
        }
        return verificationSessionViewRepository.findByOpenByLocation(
                EligibilityVerificationSessionBase.Status.Review.name()
                , districtCode
                , PageRequest.of(page, PAGE_SIZE)
        );
    }

    private boolean isCodeSet(Long code) {
        return code != null && code > 0L;
    }

    public void saveTargetingResult(TargetingResult targetingResult) {
        targetingResultRepository.save(targetingResult);
    }

    public TargetingResult findTargetingResultByHouseholdId(Long sessionId, Long household) {
        return targetingResultRepository.findByTargetingSessionAndHousehold(sessionId, household);
    }

    private Page<TargetingSessionView> getOpenTargetingSessionsByLocation(
            Long districtCode
            , Long taCode
            , Long clusterCode
            , int page
            , int pageSize
            , TargetingSessionBase.MeetingPhase meetingPhase
    ) {
        List<TargetingSessionView> slice = targetingSessionViewRepository
                .getTargetingSessionsByLocation(
                        districtCode
                        , taCode
                        , clusterCode
                        , page
                        , Math.max(pageSize, PAGE_SIZE)
                        , TargetingSessionBase.SessionStatus.Review.name()
                        , meetingPhase.name()
                );
        // TODO this is necessary for paging on the android front but can be removed to improve performance
        //  just that the app would have to be changed to use optimistic paging.
        Long totalResults = targetingSessionViewRepository.countTargetingSessionsByLocation(
                districtCode
                , taCode
                , clusterCode
                , TargetingSessionBase.SessionStatus.Review.name()
                , meetingPhase.name()
        );
        return new PageImpl<>(slice, PageRequest.of(page, pageSize), totalResults);
    }

    public Page<TargetingSessionView> getOpenTargetingSessionsForSecondCommunityMeeting(
            Long districtCode
            , Long taCode
            , Long clusterCode
            , int page
            , int pageSize) {
        return getOpenTargetingSessionsByLocation(districtCode, taCode, clusterCode, page, pageSize,
                TargetingSessionBase.MeetingPhase.second_community_meeting);
    }

    public Page<TargetingSessionView> getOpenTargetingSessionsForDistrictMeeting(
            Long districtCode
            , Long taCode
            , Long clusterCode
            , int page
            , int pageSize) {
        return getOpenTargetingSessionsByLocation(districtCode, taCode, clusterCode, page, pageSize,
                TargetingSessionBase.MeetingPhase.district_meeting);
    }

    public Page<TargetedHouseholdSummary> getTargetedHouseholdSummaries(Long sessionId, Pageable pageable) {
        return targetedHouseholdSummaryRepository.getByTargetingSession(sessionId, pageable);
    }

    @Transactional
    public boolean updateTargetedHouseholds(TargetingSessionView session, List<TargetedHouseholdStatus> statuses, @Nullable Long updatedBy) {
        var sql = """
                update targeting_results tr
                JOIN targeting_sessions ts ON ts.id = tr.targeting_session
                set tr.status = :newStatus
                    , tr.ranking = coalesce(:newRank, tr.ranking)
                	, tr.updated_at = :timestamp
                	, tr.old_status = tr.status
                	, tr.old_rank = tr.ranking
                	, tr.reason = coalesce(:changeReason, tr.reason)
                	, tr.scm_user_id = coalesce(:scmUserId, tr.scm_user_id)
                	, tr.scm_user_timestamp = coalesce(:scmTimestamp, tr.scm_user_timestamp)
                	, tr.dm_user_id = coalesce(:dmUserId, tr.dm_user_id)
                	, tr.dm_user_timestamp = coalesce(:dmTimestamp, tr.dm_user_timestamp)
                WHERE ts.id = :sessionId AND ts.status = :sessionStatus AND tr.household_id = :householdId
                """;
        boolean updated = false;

        if (statuses.isEmpty()) {
            return false;
        }

        final OffsetDateTime updatedAt = OffsetDateTime.now();

        try {
            for (TargetedHouseholdStatus status : statuses) {
                final Query query = entityManager.createNativeQuery(sql);

                query.setParameter("timestamp", updatedAt);
                query.setParameter("sessionId", session.getId());
                query.setParameter("changeReason", status.getReason());
                query.setParameter("newStatus", status.getStatus().name());
                query.setParameter("householdId", status.getHouseholdId());
                query.setParameter("sessionStatus", TargetingSessionBase.SessionStatus.Review.name());

                if (session.isAtDistrictMeeting()) {
                    query.setParameter("newRank", null);
                    query.setParameter("scmUserId", null);
                    query.setParameter("scmTimestamp", null);
                    query.setParameter("dmUserId", updatedBy);
                    query.setParameter("dmTimestamp", updatedAt);
                } else if (session.isAtSecondCommunityMeeting()) {
                    query.setParameter("dmUserId", null);
                    query.setParameter("dmTimestamp", null);
                    query.setParameter("scmUserId", updatedBy);
                    query.setParameter("scmTimestamp", updatedAt);
                    query.setParameter("newRank", status.getRank());
                }

                query.executeUpdate();
            }
            updated = true;
        } catch (Exception e) {
            LOG.error("Error during household updates", e);
        }

        return updated;
    }

    /**
     * Exports targeting session data to excel and returns path to that file.
     * @param targetingSession the session to export data for
     * @return path to the file.
     */
    public Path exportSessionDataToExcel(TargetingSessionView targetingSession, String stagingDirectory) {
        Path filePath ;
        try {
            filePath = Files.createTempFile(Paths.get(stagingDirectory), "targeted", ".xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath.toFile())) {

            Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Households"));
            // Create file headers
            Row tmpExcelRow = sheet.createRow(0);
            Cell cell = tmpExcelRow.createCell(0);
            cell.setCellValue("Targeted Households");

            // Headers
            tmpExcelRow = sheet.createRow(1);
            addCell(tmpExcelRow, 0, "FORM_NUMBER");
            addCell(tmpExcelRow, 1, "HOUSEHOLD_CODE");
            addCell(tmpExcelRow, 2, "TRADITIONAL_AUTHORITY_NAME");
            addCell(tmpExcelRow, 3, "VILLAGE CLUSTER");
            addCell(tmpExcelRow, 4, "ZONE");
            addCell(tmpExcelRow, 5, "HOUSEHOLD_HEAD_NAME");
            addCell(tmpExcelRow, 6, "HOUSEHOLD_SIZE");
            addCell(tmpExcelRow, 7, "WEALTH_QUINTILE");
            addCell(tmpExcelRow, 8, "RANKING");
            addCell(tmpExcelRow, 9, "COMMUNITY RANKING");
            addCell(tmpExcelRow, 10, "REMARKS");

            var householdList = targetedHouseholdSummaryRepository.getByTargetingSession(targetingSession.getId(), Pageable.unpaged());
            // Add other rows
            int currentRow = 2;
            for (TargetedHouseholdSummary household : householdList) {
                tmpExcelRow = sheet.createRow(currentRow);
                addCell(tmpExcelRow, 0, String.valueOf(household.getFormNumber()));
                addCell(tmpExcelRow, 1, String.valueOf(household.getMlCode()));
                addCell(tmpExcelRow, 2, household.getTa());
                addCell(tmpExcelRow, 3, household.getCluster());
                addCell(tmpExcelRow, 4, household.getZone());
                addCell(tmpExcelRow, 5, household.getHouseholdHead());
                addCell(tmpExcelRow, 6, String.valueOf(household.getMembers()));
                addCell(tmpExcelRow, 7, "");// todo: household.get("WEALTH_QUINTILE"));
                addCell(tmpExcelRow, 8, String.valueOf(household.getRanking()));
                addCell(tmpExcelRow, 9, "");
                addCell(tmpExcelRow, 10, "");

                currentRow++;
            }

            workbook.write(fos);
            tmpExcelRow = null;
            sheet = null;

            return filePath;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void addCell(Row row, int index, String data) {
        Cell cell = row.createCell(index);
        cell.setCellValue(data);
    }
}
