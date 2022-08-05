ALTER TABLE enrollment_sessions
    MODIFY mobile_review tinyint NOT NULL DEFAULT 1
        COMMENT 'Whether this session has been reviewed on the android phone'
;