-- Alter the top ups
ALTER TABLE transfer_topups CHANGE COLUMN `location_id` `district_code` BIGINT(19) NOT NULL COMMENT 'District where the topup will be applied';
