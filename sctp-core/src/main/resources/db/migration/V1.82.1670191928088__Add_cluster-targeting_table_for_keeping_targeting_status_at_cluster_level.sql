-- cluster_targeting definition

CREATE TABLE IF NOT EXISTS cluster_targeting  (
	session_id bigint NOT NULL COMMENT 'Targeting session id',
	cluster_code bigint NOT NULL COMMENT 'Cluster code',
	meeting_phase ENUM('second_community_meeting','district_approval', 'completed') NOT NULL COMMENT 'Meeting phase status',
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	closed_at timestamp,
	opened_by bigint NOT NULL COMMENT 'Id of user who made this selection',
	closed_by bigint COMMENT 'Id of user who closed this particular session',
	CONSTRAINT cluster_targeting_PK PRIMARY KEY (session_id),
	CONSTRAINT cluster_targeting_UN UNIQUE KEY (session_id,cluster_code),
	CONSTRAINT targeting_session_id_fk FOREIGN KEY (session_id) REFERENCES targeting_sessions(id),
	CONSTRAINT targeting_session_opened_by_fk FOREIGN KEY (closed_by) REFERENCES users(id),
	CONSTRAINT targeting_session_closed_by_fk FOREIGN KEY (closed_by) REFERENCES users(id)
)
COMMENT='This table stores cluster targeting information';

CREATE INDEX cluster_targeting_cluster_code_IDX USING BTREE ON cluster_targeting (cluster_code);

CREATE INDEX cluster_targeting_session_id_IDX USING BTREE ON cluster_targeting (session_id);