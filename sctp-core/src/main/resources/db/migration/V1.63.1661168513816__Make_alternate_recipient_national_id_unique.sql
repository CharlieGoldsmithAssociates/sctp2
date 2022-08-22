alter table alternate_recipient
    modify national_id varchar(8) not null
;

CREATE UNIQUE INDEX alternate_recipient_national_id_IDX USING BTREE ON alternate_recipient (national_id);
