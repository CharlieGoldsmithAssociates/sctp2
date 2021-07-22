CREATE TABLE IF NOT EXISTS funders(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    created_at timestamp NOT NULL,
    active boolean not null,
    name varchar(100) not null
);