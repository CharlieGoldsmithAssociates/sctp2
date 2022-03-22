-- This migration adjusts the lengths of the data types for the location codes to match ubr_csv_imports...
ALTER TABLE households MODIFY village_code BIGINT;
ALTER TABLE households MODIFY ta_code BIGINT;
ALTER TABLE households MODIFY cluster_code BIGINT;
ALTER TABLE households MODIFY zone_code BIGINT;
