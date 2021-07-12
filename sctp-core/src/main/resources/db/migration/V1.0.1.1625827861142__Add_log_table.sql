CREATE TABLE IF NOT EXISTS event_logs(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    created_at timestamp NOT NULL,
    archived boolean not null,
    event_type varchar(50) not null,
    log_data json not null
);