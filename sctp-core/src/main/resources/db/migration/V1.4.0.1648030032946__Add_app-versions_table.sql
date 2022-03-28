CREATE TABLE IF NOT EXISTS app_versions (
	id integer PRIMARY KEY AUTO_INCREMENT,
	version_code int NOT NULL,
	version_name varchar(100) NOT NULL,
	updated_at timestamp NOT NULL,
	mandatory_update TINYINT NOT NULL DEFAULT 1,
	file_hash varchar(300) NOT null
);

ALTER TABLE app_versions ADD INDEX(version_code);