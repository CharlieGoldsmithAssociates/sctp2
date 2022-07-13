-- Mainly used for pagination
DROP PROCEDURE IF EXISTS countDataImportDuplicates;

CREATE PROCEDURE countDataImportDuplicates(IN _session_id bigint)
    COMMENT 'Returns an import sessions duplicate count'
BEGIN
SELECT COUNT(id)
FROM ubr_csv_imports uci
JOIN (
	SELECT u.household_member_id
	FROM ubr_csv_imports u
	WHERE data_import_id = _session_id and archived = false
	GROUP BY household_member_id
	HAVING count(household_member_id) > 1
) AS t ON t.household_member_id = uci.household_member_id
WHERE uci .data_import_id = _session_id ORDER BY uci.household_member_id;
END
