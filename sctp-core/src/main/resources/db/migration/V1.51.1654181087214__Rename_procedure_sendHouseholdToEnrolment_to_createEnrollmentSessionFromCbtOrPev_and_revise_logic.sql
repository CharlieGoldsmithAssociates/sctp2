DROP PROCEDURE IF EXISTS sendHouseholdToEnrolment;
DROP PROCEDURE IF EXISTS createEnrollmentSessionFromCbtOrPev;

DELIMITER $$
CREATE PROCEDURE createEnrollmentSessionFromCbtOrPev(IN _targeting_session_id bigint, IN _verification_session_id bigint, IN _user_id bigint)
COMMENT 'This procedure creates an enrollment session, then copies households from the given pre-eligibility verification or targerting session'
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL; -- Reraise the exception
    END;

   START TRANSACTION;

	IF _targeting_session_id IS NOT NULL AND _targeting_session_id > 0 THEN
		-- Create session
		INSERT INTO enrollment_sessions
		(created_by, created_at, target_session_id, verification_session_id, district_code, ta_code, zone_code, cluster_code, program_id)
		SELECT _user_id, CURRENT_TIMESTAMP(), _targeting_session_id, NULL, ts.district_code, ts.ta_code, NULL, ts.clusters, ts.program_id
		FROM targeting_sessions ts WHERE ts.id = _targeting_session_id;

		SET @curr_timestamp = CURRENT_TIMESTAMP();
		SET @enrollment_session_id = LAST_INSERT_ID();

		-- Move the households to the enrolled_households table
		-- All households from previous modules will be brought here but only those marked as 'selected' will be shown
		INSERT INTO household_enrollment
		(session_id, household_id, status, created_at, updated_at, reviewer_id, reviewed_at)
		SELECT @enrollment_session_id, tr.household_id, tr.status, @curr_timestamp, @curr_timestamp, NULL, NULL
		FROM targeting_results tr WHERE tr.targeting_session = _targeting_session_id;

	ELSEIF _verification_session_id IS NOT NULL AND _verification_session_id > 0 THEN

		-- Create enrollment session
		INSERT INTO enrollment_sessions
		(created_by, created_at, target_session_id, verification_session_id, district_code, ta_code, zone_code, cluster_code, program_id)
		SELECT _user_id, CURRENT_TIMESTAMP(), NULL, _verification_session_id, evs.district_code, evs.ta_code, NULL, evs.clusters, evs.program_id
		FROM eligibility_verification_sessions evs WHERE evs.id = _verification_session_id;

		SET @curr_timestamp = CURRENT_TIMESTAMP();
		SET @enrollment_session_id = LAST_INSERT_ID();

		-- copy to enrollment.
		INSERT INTO household_enrollment
		(session_id, household_id, status, created_at, updated_at, reviewer_id, reviewed_at)
		SELECT @enrollment_session_id, eh.household_id, eh.selection_status, @curr_timestamp, @curr_timestamp, NULL, NULL
		FROM eligible_households eh WHERE eh.session_id = _verification_session_id;
	END IF;

	COMMIT;
END$$
DELIMITER ;