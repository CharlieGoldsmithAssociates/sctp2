-- Removes these redundant columns as they can be pulled from enrollment
ALTER TABLE transfers DROP COLUMN `district_id`;
ALTER TABLE transfers DROP COLUMN `village_cluster_id`;
ALTER TABLE transfers DROP COLUMN `traditional_authority_id`;
ALTER TABLE transfers DROP COLUMN `zone_id`;