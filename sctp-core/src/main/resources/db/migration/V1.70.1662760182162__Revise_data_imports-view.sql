DROP VIEW IF EXISTS data_imports_v;

CREATE VIEW `data_imports_v` AS
SELECT
    di.*
    ,concat_ws(' ',u.first_name, u.last_name) AS imported_by
FROM data_imports di
JOIN users u ON u.id = di.importer_user_id
;