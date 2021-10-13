DROP VIEW IF EXISTS distinct_ubr_csv_import_household_records_v;

alter table ubr_csv_imports
    add disability int comment 'Disability status code. Refer to generate IP parameters';

CREATE VIEW distinct_ubr_csv_import_household_records_v as
WITH unique_households AS (
	SELECT DISTINCT form_number, max(id) id
	FROM ubr_csv_imports uci
	WHERE validation_status = 0
	GROUP BY form_number
)
SELECT uci.*
FROM ubr_csv_imports uci
JOIN unique_households uh ON uh.id = uci.id;