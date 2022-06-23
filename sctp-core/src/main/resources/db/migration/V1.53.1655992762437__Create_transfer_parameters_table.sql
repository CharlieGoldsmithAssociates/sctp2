-- This migration adds the Transfer Parameters table
-- This migration is the first in the redesign of how parameters are handled,
-- this table will be the parent of the household and education transfer parameters
CREATE TABLE IF NOT EXISTS transfer_parameters (
	id BIGINT(19) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary system-generated key',
	title VARCHAR(50) NOT NULL COMMENT 'The title of the Parameters for easy identification and audits',
	usage_count INTEGER NOT NULL DEFAULT 0 COMMENT 'Usage count, how many times parameters have been used, cannot delete if usage > 0',
	active TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Whether the parameters are active for use or not',
	created_by INTEGER COMMENT 'User who created the parameters in the system',
	created_at TIMESTAMP NOT NULL COMMENT 'Date created in system',
	updated_at TIMESTAMP NOT NULL COMMENT 'Date modified/updated in system'
);
