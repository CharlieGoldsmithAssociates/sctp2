DROP PROCEDURE IF EXISTS getProgramFunders;

CREATE PROCEDURE getProgramFunders(IN programId bigint)
BEGIN
	SELECT f.id funderId, f.name funderName
	FROM program_funders pf
	JOIN funders f ON f.id = pf.funder_id
	WHERE pf.program_id = programId;
END ;