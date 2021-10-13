alter table ubr_csv_imports
    modify form_number bigint comment 'UBR code as a number, without the UBR prefix. Useful for indexing';