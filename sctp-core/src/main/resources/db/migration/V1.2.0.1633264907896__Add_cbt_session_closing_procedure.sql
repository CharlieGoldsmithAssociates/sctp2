DROP PROCEDURE IF EXISTS closeCommunityBasedTargetingSession;

DELIMITER $$

CREATE PROCEDURE closeCommunityBasedTargetingSession(IN sessionId bigint, IN closedBy bigint, IN closedAt timestamp, IN newStatus varchar(20))
comment 'Close targeting session'
BEGIN
	UPDATE targeting_sessions ts
	SET ts.status = newStatus
		,ts.closed_by = closedBy
		,ts.closed_at = closedAt
	WHERE ts.id = sessionId;
END$$

DELIMITER ;