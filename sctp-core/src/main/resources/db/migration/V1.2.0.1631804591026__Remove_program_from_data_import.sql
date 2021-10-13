--
-- Remove program reference because data import does not directly go to programs rather population set
--
DROP VIEW IF EXISTS data_imports_v;

ALTER TABLE data_imports
    DROP FOREIGN KEY program_id_fk;

ALTER TABLE data_imports
    DROP COLUMN program_id;

CREATE VIEW data_imports_v AS
SELECT
    di.*
    ,concat_ws(' ', u.first_name, u.last_name) AS imported_by
FROM data_imports di
JOIN users u ON u.id = di.importer_user_id;