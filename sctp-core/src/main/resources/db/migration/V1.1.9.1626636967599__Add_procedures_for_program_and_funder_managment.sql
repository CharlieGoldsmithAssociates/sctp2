DROP PROCEDURE IF EXISTS getAvailableProgramFunders;
DROP PROCEDURE IF EXISTS isFunderAssignedToProgram;

CREATE PROCEDURE getAvailableProgramFunders(IN programId bigint)
BEGIN
	SELECT f.*
	FROM funders f
	WHERE f.active = 1 AND f.id NOT IN (
		SELECT pf.funder_id
		FROM program_funders pf
		WHERE pf.program_id = programId
	);
END ;

CREATE PROCEDURE isFunderAssignedToProgram(IN programId bigint, IN funderId bigint)
BEGIN
	SELECT EXISTS (SELECT 1 FROM program_funders pf WHERE pf.program_id = programId AND pf.funder_id = funderId)
	AS isAssigned;
END ;