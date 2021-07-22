DROP PROCEDURE IF EXISTS addProgramFunders;

CREATE PROCEDURE addProgramFunders(IN programId bigint, IN funders_ids varchar(4096))
BEGIN
	INSERT INTO program_funders (program_id, created_at, funder_id)
	/* Following CTE verifies that funders are active and do indeed exist */
	WITH _funders (id) AS (SELECT f.id FROM funders f WHERE f.active = 1 AND FIND_IN_SET(f.id, funders_ids) != 0)
	SELECT programId, CURRENT_TIMESTAMP(), _funders.id
	FROM _funders
	WHERE _funders.id NOT IN(SELECT pf.funder_id FROM program_funders pf WHERE pf.program_id = programId);
END;