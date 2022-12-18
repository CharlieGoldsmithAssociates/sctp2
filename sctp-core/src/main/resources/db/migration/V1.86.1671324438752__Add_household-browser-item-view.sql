DROP VIEW IF EXISTS household_browser_item_view;

CREATE VIEW household_browser_item_view
AS
SELECT h.household_id
, h2.ubr_code form_number
, h2.ml_code
, count(i.household_id) member_count
, h.village_code
, (CASE WHEN count(i.household_id) = 0 THEN (SELECT '[]') ELSE (
	SELECT
	json_arrayagg(json_object('id', i.id, 'name', CONCAT(i.first_name, ' ', i.last_name), 'gender', i.gender, 'dob', i.date_of_birth, 'age', i.age, 'rel', i.relationship_to_head, 'individual_id', upper(i.individual_id), 'member_code', i.member_code)) members
) END) members
FROM households h
JOIN households h2 ON h2.household_id = h.household_id
LEFT JOIN individuals_view i ON i.household_id = h.household_id
GROUP BY household_id
;