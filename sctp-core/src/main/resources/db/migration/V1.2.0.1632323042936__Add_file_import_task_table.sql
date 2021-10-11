CREATE TABLE IF NOT EXISTS file_import_tasks (
	id bigint NOT NULL AUTO_INCREMENT,
	data_import_id bigint NOT NULL,
	created_at timestamp NOT NULL,
	finished_at timestamp,
	current_row bigint NOT NULL comment 'If the task gets interrupted, it will restart from this ',
	row_count bigint DEFAULT 0,
	status varchar(20) NOT NULL comment 'queued, processing, done',


	CONSTRAINT file_import_tasks_pk PRIMARY key(id),
	CONSTRAINT data_import_fk FOREIGN KEY (data_import_id) REFERENCES data_imports(id)
);

DROP VIEW IF EXISTS file_import_tasks_v;
CREATE VIEW file_import_tasks_v
AS
SELECT
	fis.*
	,di.source_file
FROM file_import_tasks fis
JOIN data_imports di ON di.id = fis.data_import_id;