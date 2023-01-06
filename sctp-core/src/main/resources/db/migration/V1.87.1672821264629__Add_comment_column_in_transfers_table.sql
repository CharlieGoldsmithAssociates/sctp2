-- Adds comment column to the transfers table.
ALTER table transfers
    ADD COLUMN comment varchar(200) after disbursed_by_user_id;