DROP TABLE IF EXISTS program_funders;

ALTER TABLE programs modify id BIGINT NOT NULL AUTO_INCREMENT;

-- Restore
CREATE TABLE IF NOT EXISTS program_funders (
    program_id bigint not null,
    funder_id bigint not null,
    created_at timestamp not null,
    foreign key (program_id) references programs (id),
    foreign key (funder_id) references funders (id),

    primary key(program_id, funder_id)
);