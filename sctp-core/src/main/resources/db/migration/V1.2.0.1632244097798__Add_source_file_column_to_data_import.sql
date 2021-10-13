--
-- Add `source_file` column to data_imports table to indicate name of source file.
--
DROP VIEW IF EXISTS data_imports_v;

ALTER TABLE data_imports
    ADD COLUMN source_file varchar(256) after data_source;

CREATE VIEW data_imports_v AS
SELECT
    di.*
    ,concat_ws(' ', u.first_name, u.last_name) AS imported_by
FROM data_imports di
JOIN users u ON u.id = di.importer_user_id;