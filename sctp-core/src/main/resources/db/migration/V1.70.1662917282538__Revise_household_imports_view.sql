DROP VIEW IF EXISTS household_imports;

CREATE VIEW household_imports
AS
SELECT uci.household_id
, ANY_VALUE(form_number) form_number
, ANY_VALUE(household_ml_code) ml_code
, SUM(CASE WHEN relationship_to_head = 1 THEN relationship_to_head  ELSE 0 end) > 0 has_household_head
, SUM(CASE WHEN validation_status = 'Error' THEN 1 ELSE 0 END) error_count
, SUM(CASE WHEN archived THEN 1 ELSE 0 END) > 0 archived
, COUNT(uci.household_id) member_count
, ANY_VALUE(data_import_id) data_import_id
, ANY_VALUE(CASE WHEN relationship_to_head = 1 THEN concat(first_name, ' ', last_name) END) household_head_name
, ANY_VALUE(CASE WHEN relationship_to_head = 1 THEN gender end) household_head_gender
, ANY_VALUE(CASE WHEN relationship_to_head = 1 THEN date_of_birth end) household_head_dob
, ANY_VALUE(CASE WHEN relationship_to_head = 1 THEN national_id end) household_head_id
, ANY_VALUE(district_name) district_name
, ANY_VALUE(traditional_authority_name) traditional_authority_name
, ANY_VALUE(village_name) village_name
, ANY_VALUE(group_village_head_name) group_village_head_name
, ANY_VALUE(cluster_name) cluster_name
FROM ubr_csv_imports uci
GROUP BY uci.household_id
;