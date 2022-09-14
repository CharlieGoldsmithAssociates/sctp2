DROP VIEW IF EXISTS household_import_stats;

CREATE VIEW household_import_stats
AS
SELECT
ANY_VALUE(data_import_id) data_import_id
,sum(CASE WHEN has_household_head = FALSE THEN 1 ELSE 0 END) no_head
,sum(CASE WHEN archived = 1 THEN 1 ELSE 0 end) archived
FROM household_imports GROUP BY data_import_id
;