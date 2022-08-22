alter table school_enrolled
ADD CONSTRAINT enrollment_id UNIQUE (household_id, individual_id)
;