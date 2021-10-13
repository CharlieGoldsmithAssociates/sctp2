DROP PROCEDURE IF EXISTS getDataImportDuplicates;

CREATE PROCEDURE getDataImportDuplicates(IN _session_id bigint, IN page int, IN page_size int)
BEGIN 
SELECT * 
FROM ubr_csv_imports uci
JOIN (
	SELECT national_id
	FROM ubr_csv_imports
	WHERE data_import_id = _session_id
	GROUP BY national_id 
	HAVING count(national_id) > 1
) AS t ON t.national_id = uci.national_id
WHERE uci .data_import_id  = _session_id ORDER BY household_id LIMIT page, page_size ;
END