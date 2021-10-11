--
-- Table for keeping track of import sessions
--

CREATE TABLE IF NOT EXISTS data_imports (
  id bigint NOT NULL AUTO_INCREMENT,
  program_id bigint NOT NULL,
  title varchar(200) NOT NULL COMMENT 'Title description for the import',
  households bigint DEFAULT '0' COMMENT 'Number of households in the batch',
  individuals bigint DEFAULT '0' COMMENT 'Number of individuals in the batch',
  data_source varchar(100) NOT NULL,
  status varchar(50) NOT NULL COMMENT 'Import process status',
  status_text varchar(100) DEFAULT NULL,
  importer_user_id bigint NOT NULL,
  batch_duplicates bigint DEFAULT '0' COMMENT 'Duplicates in batch',
  population_duplicates bigint DEFAULT '0' COMMENT 'Duplicates against population',
  import_date timestamp NOT NULL,
  completion_date timestamp NULL DEFAULT NULL COMMENT 'Date when the import completed.',

  PRIMARY KEY (`id`),
  KEY `program_id_fk` (`program_id`),
  KEY `user_id_fk` (`importer_user_id`),
  CONSTRAINT `program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `programs` (`id`),
  CONSTRAINT `user_id_fk` FOREIGN KEY (`importer_user_id`) REFERENCES `users` (`id`)
);

--
-- View
--

DROP VIEW IF EXISTS data_imports_v;

CREATE VIEW data_imports_v AS
SELECT
    di.*
    , p.name AS program_name
    ,concat_ws(' ', u.first_name, u.last_name) AS imported_by
FROM data_imports di
JOIN users u ON u.id = di.importer_user_id
JOIN programs p ON p.id = di.program_id;