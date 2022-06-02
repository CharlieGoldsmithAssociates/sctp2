ALTER TABLE enrollment_sessions
    ADD COLUMN program_id bigint comment 'linked program id'
;

CREATE INDEX idx_program_id ON enrollment_sessions(program_id);

alter table enrollment_sessions
    add constraint fk_program_id foreign key(program_id) references programs(id)
;