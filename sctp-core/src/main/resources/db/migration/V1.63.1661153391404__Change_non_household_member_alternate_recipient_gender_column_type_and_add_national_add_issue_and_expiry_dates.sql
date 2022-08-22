alter table alternate_recipient
    modify gender varchar(10) not null,
    add column id_issue_date date not null after national_id,
    add column id_expiry_date date not null
;