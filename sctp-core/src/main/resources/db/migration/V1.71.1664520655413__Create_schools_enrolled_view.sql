DROP VIEW IF EXISTS school_enrolled_v;

CREATE VIEW school_enrolled_v
AS
SELECT se.*
,s.name school_name
,s.code school_code
,CONCAT(i.first_name,' ',i.last_name) individual_name
,i.member_code household_member_code
FROM school_enrolled se
JOIN schools s ON se.school_id = s.id
JOIN individuals i ON se.individual_id = i.id
;