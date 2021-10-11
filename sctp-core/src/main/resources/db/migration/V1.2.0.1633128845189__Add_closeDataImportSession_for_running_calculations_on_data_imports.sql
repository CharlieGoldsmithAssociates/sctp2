--
-- Add closeDataImportSession
--

DROP PROCEDURE IF EXISTS closeDataImportSession;

CREATE PROCEDURE closeDataImportSession(IN _session_id bigint) comment 'Runs final checks (duplicates, count) on the staged data and updates the import session'
BEGIN
UPDATE data_imports di
SET di.individuals = (SELECT count(id) total FROM ubr_csv_imports uci WHERE data_import_id = _session_id)
	,di.households = (SELECT count(DISTINCT form_number) total FROM ubr_csv_imports WHERE data_import_id = _session_id)
	,di.batch_duplicates = (SELECT count(DISTINCT national_id) total FROM ubr_csv_imports WHERE data_import_id = _session_id)
WHERE di.id = _session_id;
END