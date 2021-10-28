create table if not exists criteria_filters (
    id bigint primary key auto_increment,
    template_id bigint NOT NULL,
    conjunction enum ('None', 'And', 'Or') NOT NULL,
    criterion_id bigint NOT NULL,
    filter_value varchar(1024),

    CONSTRAINT template_id_fk FOREIGN KEY(template_id) REFERENCES filter_templates(id),
    CONSTRAINT criterion_id_fk FOREIGN KEY(criterion_id) REFERENCES targeting_criteria(id)
) comment 'Contains the actual filters that will be used to';
