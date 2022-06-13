DROP VIEW IF EXISTS school_children_candidates_v;

CREATE VIEW school_children_candidates_v
AS
SELECT i.id
,i.individual_id
, upper(CONCAT(i.first_name, ' ', i.last_name)) name
, date_of_birth
, TIMESTAMPDIFF(YEAR, i.date_of_birth, CURDATE()) age
, i.household_id
, i.created_at
, i.modified_at
FROM individuals i
-- Actual range is 5-22 (however, 25 year olds can also qualify as children if they are in school)
WHERE TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) >= 5 AND TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) <= 25
AND NOT EXISTS (SELECT individual_id FROM school_enrolled WHERE household_id = i.household_id)
ORDER BY age ASC
;