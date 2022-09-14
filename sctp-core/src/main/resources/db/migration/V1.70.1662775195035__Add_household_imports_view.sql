DROP VIEW IF EXISTS household_imports;

CREATE VIEW household_imports
AS
SELECT household_id
, MAX(form_number) form_number
, SUM(CASE WHEN relationship_to_head = 1 THEN relationship_to_head  ELSE 0 end) > 0 has_household_head
, COUNT(household_id) member_count
, GROUP_CONCAT(DISTINCT CASE WHEN relationship_to_head = 1 THEN concat(first_name, ' ', last_name) end) household_head_name
, GROUP_CONCAT(DISTINCT CASE WHEN relationship_to_head = 1 THEN date_of_birth end) household_head_dob
, GROUP_CONCAT(DISTINCT CASE WHEN relationship_to_head = 1 THEN gender end) household_head_gender
, GROUP_CONCAT(DISTINCT CASE WHEN relationship_to_head = 1 THEN national_id end) household_head_id
, (SELECT data_import_id FROM ubr_csv_imports WHERE household_id = uci.household_id LIMIT 1) data_import_id
FROM ubr_csv_imports uci
GROUP BY household_id
;