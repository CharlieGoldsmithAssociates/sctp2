alter table alternate_recipient
    add column created_at timestamp default current_timestamp(),
    add column modified_at timestamp
;