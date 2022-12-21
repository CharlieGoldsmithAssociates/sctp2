-- individuals
CREATE INDEX individuals_household_id_IDX USING BTREE ON individuals (household_id);

-- households
CREATE INDEX households_location_code_IDX USING BTREE ON households (location_code);
CREATE INDEX households_village_code_IDX USING BTREE ON households (village_code);
CREATE INDEX households_ml_code_IDX USING BTREE ON households (ml_code);
CREATE INDEX households_ubr_code_IDX USING BTREE ON households (ubr_code);
CREATE INDEX households_ta_code_IDX USING BTREE ON households (ta_code);
CREATE INDEX households_cluster_code_IDX USING BTREE ON households (cluster_code);
CREATE INDEX households_zone_code_IDX USING BTREE ON households (zone_code);
CREATE INDEX households_group_village_head_code_IDX USING BTREE ON households (group_village_head_code);

-- locations
CREATE INDEX locations_location_type_IDX USING BTREE ON locations (location_type);
CREATE INDEX locations_parent_id_IDX USING BTREE ON locations (parent_id);

-- targeting results
CREATE INDEX targeting_results_status_IDX USING BTREE ON targeting_results (status);

-- Add foreign keys (implicit indexes)
ALTER TABLE targeting_sessions ADD CONSTRAINT targeting_sessions_district_code_FK FOREIGN KEY (district_code) REFERENCES locations(code);
ALTER TABLE targeting_sessions ADD CONSTRAINT targeting_sessions_ta_code_FK FOREIGN KEY (ta_code) REFERENCES locations(code);
ALTER TABLE targeting_sessions ADD CONSTRAINT targeting_sessions_program_id_FK FOREIGN KEY (program_id) REFERENCES programs(id);

