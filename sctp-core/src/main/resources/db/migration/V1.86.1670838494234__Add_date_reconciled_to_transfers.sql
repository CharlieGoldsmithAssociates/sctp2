-- Adds date_reconciled to the transfers table.
ALTER TABLE transfers ADD COLUMN date_reconciled date NULL COMMENT 'Date when the transfer was reconciled' AFTER is_reconciled ;