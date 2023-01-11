-- Adds is_reviewed to the transfers table.
ALTER TABLE transfers ADD COLUMN is_reviewed TINYINT(1) DEFAULT 0 COMMENT 'Whether the transfer is reviewed/recorded' AFTER created_by ;