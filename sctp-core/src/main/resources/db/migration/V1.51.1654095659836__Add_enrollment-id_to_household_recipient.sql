ALTER TABLE household_recipient
    ADD COLUMN enrollment_session bigint comment 'linked session id'
;

CREATE INDEX idx_enrollment_session_id ON household_recipient(enrollment_session);

alter table household_recipient
    add constraint fk_enrollment_session foreign key(enrollment_session) references enrollment_sessions(id)
;