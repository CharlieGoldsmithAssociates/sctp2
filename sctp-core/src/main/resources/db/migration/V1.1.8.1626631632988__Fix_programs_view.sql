DROP VIEW IF EXISTS program_info_v;

CREATE VIEW program_info_v
AS
SELECT 
	p.id
	,p.active
	, p.start_date as startDate
	, p.end_date as endDate
	, p.name
	, COUNT(pf.program_id) AS funderCount
	, l.name AS location
FROM programs p
JOIN locations l ON l.id = p.location
LEFT JOIN program_funders pf ON pf.program_id = p.id
GROUP BY p.id, pf.program_id
;