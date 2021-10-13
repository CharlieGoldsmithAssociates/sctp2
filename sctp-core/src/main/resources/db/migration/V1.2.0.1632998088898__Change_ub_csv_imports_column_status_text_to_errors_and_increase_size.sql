--
-- Change status_text column to errors. This will contain errors (comma separated list) for that particular row, if any.
--

alter table ubr_csv_imports
    change status_text errors varchar(1024);