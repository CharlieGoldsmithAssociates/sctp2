DROP VIEW IF EXISTS program_info_v;

CREATE VIEW program_info_v
AS
SELECT
	p.id
	,p.active
	, p.start_date as startDate
	, p.end_date as endDate
	, p.name
	, pf.funderCount
	, pu.userCount
	, l.name AS location
FROM programs p
JOIN locations l ON l.id = p.location
LEFT JOIN (SELECT program_id, count(funder_id) funderCount FROM program_funders GROUP BY program_id) pf ON pf.program_id = p.id
LEFT JOIN (SELECT program_id, count(user_id) userCount FROM program_users GROUP BY program_id) pu ON pu.program_id = p.id
GROUP BY p.id
;