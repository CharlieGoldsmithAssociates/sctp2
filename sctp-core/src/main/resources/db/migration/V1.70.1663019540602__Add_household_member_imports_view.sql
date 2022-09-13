DROP VIEW IF EXISTS household_member_imports;

CREATE VIEW household_member_imports
AS
SELECT
household_member_id
,data_import_id
,household_id
,concat(first_name, ' ', last_name) name
,gender
,date_of_birth
,UPPER(national_id) national_id
,relationship_to_head relationship
FROM ubr_csv_imports uci
WHERE household_member_id IS NOT NULL
;