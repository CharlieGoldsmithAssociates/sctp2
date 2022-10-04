ALTER TABLE enrollment_sessions
    CHANGE status
    status enum('review','closed') NOT NULL DEFAULT 'review'
    COMMENT 'status of this enrollment session'
;