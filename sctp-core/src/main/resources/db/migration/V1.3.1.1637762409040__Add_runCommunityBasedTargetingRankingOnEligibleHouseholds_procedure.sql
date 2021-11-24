DROP PROCEDURE IF EXISTS runCommunityBasedTargetingRankingOnEligibleHouseholds;

DELIMITER $$
CREATE PROCEDURE runCommunityBasedTargetingRankingOnEligibleHouseholds(IN _cbt_session_id bigint, IN _pev_session_id bigint)
    COMMENT 'This procedure will rank households based on the pmt_score from UBR. Unlike `runCommunityBasedTargetingRanking` which uses the entire population set, the source of the household list will come from the pre-eligibilty verification session, whose id is passed here.'
BEGIN

	CREATE TEMPORARY TABLE cbt_selection (id_as_rank int PRIMARY KEY AUTO_INCREMENT)
	AS
	SELECT h.pmt_score, h.ubr_code, h.household_id, _cbt_session_id AS cbt_session_id
	FROM eligible_households eh
	JOIN households h ON h.household_id = eh.household_id
	WHERE eh.session_id = _pev_session_id
	ORDER BY h.pmt_score ASC;

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

END $$

DELIMITER ;
