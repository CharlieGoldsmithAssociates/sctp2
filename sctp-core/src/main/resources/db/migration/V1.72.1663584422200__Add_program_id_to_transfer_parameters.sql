-- COMMENT 'Program ID for which the parameters are linked';
ALTER TABLE transfer_parameters ADD COLUMN program_id BIGINT(19) NOT NULL AFTER id ;
