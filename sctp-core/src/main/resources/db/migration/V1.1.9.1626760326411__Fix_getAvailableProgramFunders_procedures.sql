DROP PROCEDURE IF EXISTS getAvailableProgramFunders;

CREATE PROCEDURE getAvailableProgramFunders(IN programId bigint)
BEGIN
	SELECT f.id funderId, f.name funderName
	FROM funders f
	WHERE f.active = 1 AND f.id NOT IN (
		SELECT pf.funder_id
		FROM program_funders pf
		WHERE pf.program_id = programId
	);
END ;
