-- Alters the Education Zones table to use Location Codes instead of the IDs as foreign keys
ALTER TABLE education_zones CHANGE ta_id ta_code BIGINT(19) NOT NULL COMMENT 'Traditional Authority where the Education Zone is';
ALTER TABLE education_zones CHANGE district_id district_code BIGINT(19) NOT NULL COMMENT 'District where the Education Zone is';

ALTER TABLE education_zones ADD CONSTRAINT fk_education_zone_ta FOREIGN KEY (ta_code) REFERENCES locations(code);
ALTER TABLE education_zones ADD CONSTRAINT fk_education_zone_district FOREIGN KEY (district_code) REFERENCES locations(code);