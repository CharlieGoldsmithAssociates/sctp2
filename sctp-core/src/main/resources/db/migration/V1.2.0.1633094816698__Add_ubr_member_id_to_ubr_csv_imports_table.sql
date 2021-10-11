alter table ubr_csv_imports
    add household_member_id bigint comment 'UBR-database assigned index. Used for detecting duplicates during import.';