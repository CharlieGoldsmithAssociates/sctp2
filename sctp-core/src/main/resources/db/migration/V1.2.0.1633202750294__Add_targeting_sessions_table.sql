CREATE TABLE IF NOT EXISTS targeting_sessions (
	id bigint AUTO_INCREMENT,
	created_at timestamp,
	closed_at timestamp,
	closed_by bigint,
	created_by bigint,
	district_code bigint,
	program_id bigint,
	ta_code bigint,
	status varchar(15) NOT NULL comment 'Status of the session: review, completed.',
	clusters varchar(4096) comment 'Holds cluster ids. Comma separated',
	CONSTRAINT creator_fk FOREIGN key(created_by) REFERENCES users(id),
	PRIMARY KEY (id)
);