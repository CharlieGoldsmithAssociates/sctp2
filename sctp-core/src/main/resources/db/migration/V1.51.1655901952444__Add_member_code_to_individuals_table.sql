alter table individuals
    add column member_code varchar(16) comment 'Household member code, prefixed-by the household ML code. Stored for display only'
;