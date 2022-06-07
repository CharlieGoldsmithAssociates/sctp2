-- FROM comment by CK: And I just checked, the source_file column is varchar(256). Maybe increase it also in case the json grows.
ALTER TABLE `data_imports`
	CHANGE COLUMN `source_file` `source_file` TEXT NOT NULL COMMENT 'Source file or parameters used for import' AFTER `data_source`;
