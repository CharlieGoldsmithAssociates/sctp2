
alter table ubr_csv_imports
    change household_code household_ml_code bigint comment 'ML code for this household. Can be null for new members',
    change form_number form_number bigint not null comment 'UBR assigned ID.';