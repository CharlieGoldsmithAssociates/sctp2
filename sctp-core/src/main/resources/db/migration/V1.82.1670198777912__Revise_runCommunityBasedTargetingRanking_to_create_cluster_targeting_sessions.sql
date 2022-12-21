DROP PROCEDURE IF EXISTS runCommunityBasedTargetingRanking;

CREATE PROCEDURE runCommunityBasedTargetingRanking(IN _session_id bigint)
    COMMENT 'This procedure will rank households based on the pmt_score from UBR.'
BEGIN
	DECLARE _user_id BIGINT;
	DECLARE _clusters LONGTEXT;
	DECLARE _timestamp TIMESTAMP;
	
	INSERT INTO targeting_results (targeting_session,household_id,ranking,pmt_score,status,created_at,updated_at)
	SELECT
		_session_id AS targeting_session
		, h.household_id
		, ROW_NUMBER() OVER(ORDER BY h.pmt_score ASC) AS ranking
		, h.pmt_score
		, 'PreEligible' AS status
		, CURRENT_TIMESTAMP() created_at 
		, CURRENT_TIMESTAMP() updated_at
	FROM targeting_sessions ts
	JOIN households h ON h.location_code = ts.district_code
	JOIN non_empty_households_v neh on neh.household_id = h.household_id
	WHERE FIND_IN_SET(concat("", h.cluster_code), ts.clusters) AND ts.id = _session_id
	ORDER BY h.pmt_score ASC;

	-- Get some initial parameters from session
	SELECT ts.created_by, ts.created_at, ts.clusters 
	INTO _user_id, _timestamp, _clusters
	FROM targeting_sessions ts 
	WHERE ts.id = _session_id;

	-- insert into cluster_targeting 
	INSERT INTO cluster_targeting(session_id,cluster_code,meeting_phase,created_at,updated_at,opened_by)
	SELECT _session_id, c.code, 'second_community_meeting', _timestamp, _timestamp, _user_id
	FROM (SELECT 1) t
	JOIN targeted_clusters_view c ON c.session_id = _session_id;
END