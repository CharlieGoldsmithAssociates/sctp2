-- Renames location_id to district_code
ALTER TABLE transfer_topups CHANGE location_id district_code BIGINT(19) NOT NULL COMMENT 'District code where the topups will be applied';

ALTER TABLE transfer_topups ADD COLUMN ta_codes TEXT NULL COMMENT 'Optional: CSV of T/A codes where the topups will be applied' AFTER district_code;

ALTER TABLE transfer_topups ADD COLUMN cluster_codes TEXT NULL COMMENT 'Optional: CSV of Cluster codes where the topups will be applied' AFTER ta_codes;

ALTER TABLE transfer_topups ADD CONSTRAINT fk_transfer_topup_district FOREIGN KEY (district_code) REFERENCES locations(code);