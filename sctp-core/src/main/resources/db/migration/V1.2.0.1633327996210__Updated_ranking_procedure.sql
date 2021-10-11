DROP PROCEDURE IF EXISTS runCommunityBasedTargetingRanking;

delimiter $$

CREATE  PROCEDURE runCommunityBasedTargetingRanking(IN _session_id bigint)
    COMMENT 'This procedure will rank households based on the pmt_score from UBR. The pmt_score will be frozen for cbt until the procedure is called again.'
BEGIN

	/* Perform the ranking by utilizing a temporary table */
	CREATE TEMPORARY TABLE cbt_selection (id_as_rank int PRIMARY KEY AUTO_INCREMENT)
	AS
	SELECT pmt_score, ubr_code, household_id, _session_id AS cbt_session_id
		FROM targeting_sessions ts
		JOIN households h ON h.location_code = ts.district_code
		WHERE FIND_IN_SET(concat("",h.cluster_code), ts.clusters) AND ts.id  = _session_id
		ORDER BY pmt_score ASC;

	/* Update the selection in the household table to reflect the selection status */
	UPDATE households h
	INNER JOIN cbt_selection s ON s.household_id = h.household_id
	SET h.cbt_rank = s.id_as_rank
		,h.cbt_pmt = s.pmt_score
		,h.cbt_session_id = s.cbt_session_id
		,h.cbt_selection = TRUE
		,h.cbt_status = 1
		,h.last_cbt_ranking = CURRENT_TIMESTAMP()
	;

	/* Gracefully delete the temporary table */
	DROP TEMPORARY TABLE IF EXISTS cbt_selection;
END$$

delimiter ;