ALTER TABLE enrollment_sessions
    ADD mobile_review tinyint NOT NULL DEFAULT 0 COMMENT 'Whether this session has been reviewed on the android phone',
    ADD mobile_review_at timestamp,
    ADD mobile_reviewer bigint comment 'Id of user who reviewed this session on the android app'
;