CREATE TABLE location_import_session (
	id bigint auto_increment NOT NULL,
	start_date timestamp NOT NULL COMMENT 'When the import task started',
	end_date timestamp NULL COMMENT 'When the import task ended',
	status enum('Downloading', 'Error', 'Downloaded') NOT NULL COMMENT 'Status of the task.',
	user_id bigint NOT NULL COMMENT 'Id of the user that initiated the import task',
	status_text varchar(1024) NULL COMMENT 'Extra status text (errors, info, warning message)',
	reported_count bigint DEFAULT 0 NOT NULL COMMENT 'Number of locations returned in the response as indicated in the headers by the UBR API',
	imported_count bigint DEFAULT 0 NOT NULL COMMENT 'Actual number of locations imported',
	CONSTRAINT location_import_session_PK PRIMARY KEY (id),
	CONSTRAINT location_import_session_user_id_FK FOREIGN KEY (user_id) REFERENCES users(id)
)
COMMENT='Table for tracking ubr location synchronization. Locations are directly inserted/upserted into the locations table';