alter table individuals
    add ubr_csv_batch_number bigint comment 'This number will identify and link individuals to batch source';

alter table household_members
    add ubr_csv_batch_number bigint comment 'This number will identify and link household_members to batch source';

alter table ubr_csv_imports
    add batch_number bigint comment 'Batch source number. Consists of user id, timestamp, and data-import id';

alter table households
    add ubr_csv_batch_number bigint comment 'This number will identify and link households to their import batch source';
