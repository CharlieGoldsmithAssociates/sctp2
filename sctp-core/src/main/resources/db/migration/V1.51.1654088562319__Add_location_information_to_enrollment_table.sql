ALTER TABLE enrollment_sessions
    ADD COLUMN closed_by bigint comment 'user id that closed the session',
    ADD COLUMN closed_at timestamp comment 'when the session was closed',
    ADD COLUMN status enum('review', 'closed') comment 'status of this enrollment session',
    ADD COLUMN district_code bigint,
    ADD COLUMN ta_code bigint,
    ADD COLUMN zone_code bigint,
    ADD COLUMN cluster_code longtext
;

CREATE INDEX idx_created_by ON enrollment_sessions(created_by);
CREATE INDEX idx_closed_by ON enrollment_sessions(closed_by);

CREATE INDEX idx_district_code ON enrollment_sessions(district_code);
CREATE INDEX idx_ta_code ON enrollment_sessions(ta_code);
CREATE INDEX idx_zone_code ON enrollment_sessions(zone_code);


alter table enrollment_sessions
    add constraint fk_created_by foreign key (created_by) references users(id)
;