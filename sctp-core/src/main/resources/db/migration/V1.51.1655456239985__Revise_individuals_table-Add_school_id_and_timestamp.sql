alter table individuals
    add column school_id bigint comment 'School id of the school that the person is enrolled in. This information does not come from UBR.',
    add column school_info_updated_at timestamp comment 'timestamp of when the update was done'
;

alter table individuals
    add constraint individual_school_id_fk foreign key(school_id) references schools(id)
;