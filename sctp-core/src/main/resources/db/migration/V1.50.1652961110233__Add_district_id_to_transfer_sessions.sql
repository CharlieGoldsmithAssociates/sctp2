-- Adds District ID to the Transfer Sessions table
ALTER TABLE transfers_sessions ADD COLUMN district_id BIGINT(19) NOT NULL AFTER program_id;