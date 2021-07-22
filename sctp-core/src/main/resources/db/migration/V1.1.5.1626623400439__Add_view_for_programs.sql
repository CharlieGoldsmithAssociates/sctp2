DROP VIEW IF EXISTS program_info_v;

CREATE VIEW program_info_v
AS
SELECT 
	p.id
	,p.active
	, p.start_date
	, p.end_date
	, p.name
	, COUNT(pf.program_id) AS funder_count 
	, l.name AS location
FROM programs p
JOIN locations l ON l.id  = p.location 
JOIN program_funders pf ON pf.program_id = p.id
GROUP BY pf.program_id 
;