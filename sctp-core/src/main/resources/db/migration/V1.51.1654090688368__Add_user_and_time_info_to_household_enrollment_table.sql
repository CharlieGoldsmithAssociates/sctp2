ALTER TABLE household_enrollment
    ADD COLUMN created_at timestamp,
    ADD COLUMN updated_at timestamp,
    ADD COLUMN reviewer_id bigint comment 'user who did the review',
    ADD COLUMN reviewed_at timestamp comment 'when this household was reviewed'
;

CREATE INDEX idx_reviewer_id ON household_enrollment(reviewer_id);

alter table household_enrollment
    add constraint fk_reviewed_by foreign key(reviewer_id) references users(id)
;