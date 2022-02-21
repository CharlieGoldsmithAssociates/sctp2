CREATE TABLE transfers_sessions (
    `id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `program_id` BIGINT NOT NULL COMMENT  'Program ID for the sessions - caching here to avoid superfluos joins',
    `enrollment_session_id` BIGINT NOT NULL COMMENT 'Enrollment Session ID which the session was initiated from',
    `active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Whether the parameter is active(1) or disabled(0)',
	`created_at` TIMESTAMP NULL DEFAULT NULL,
	`modified_at` datetime DEFAULT NULL
);