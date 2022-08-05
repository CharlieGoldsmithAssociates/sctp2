DROP VIEW IF EXISTS enrollment_session_v;

CREATE VIEW enrollment_session_v
AS
SELECT es.*
,CONCAT(u.first_name,' ',u.last_name) creator_name
,CONCAT(u2.first_name,' ',u2.last_name) closer_name
,CONCAT(u3.first_name,' ',u3.last_name) reviewer_name
,d.name district_name
,t.name ta_name
,p.name program_name
,(SELECT COUNT(id) FROM household_enrollment WHERE session_id = es.id) AS household_count
,(SELECT COUNT(household_id) FROM household_enrollment WHERE session_id = es.id AND reviewer_id IS NOT NULL AND reviewed_at IS NOT NULL) reviewed_households
FROM enrollment_sessions es
JOIN users u ON u.id = es.created_by
LEFT JOIN users u2 ON u2.id = es.closed_by
LEFT JOIN users u3 ON u3.id = es.mobile_reviewer
JOIN locations d ON d.code = es.district_code
JOIN locations t ON t.code = es.ta_code
JOIN programs p ON p.id = es.program_id
;