alter table household_recipient
    add column main_photo_type varchar(30),
    add column alt_photo_type varchar(30)
;

alter table household_recipient
    modify main_recipient bigint
;

alter table household_recipient
    modify alt_recipient bigint
;