-- Removes the locations fields which will be referenced directly from the households
ALTER TABLE transfers DROP COLUMN district_id;
ALTER TABLE transfers DROP COLUMN zone_id;
ALTER TABLE transfers DROP COLUMN traditional_authority_id;
ALTER TABLE transfers DROP COLUMN village_cluster_id;
