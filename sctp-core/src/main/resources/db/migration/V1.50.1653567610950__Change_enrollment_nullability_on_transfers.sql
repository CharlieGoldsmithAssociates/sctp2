ALTER TABLE `transfers_sessions` CHANGE COLUMN `enrollment_session_id` `enrollment_session_id` BIGINT(19) NULL COMMENT 'Enrollment Session ID which the session was initiated from' AFTER `program_id`;
