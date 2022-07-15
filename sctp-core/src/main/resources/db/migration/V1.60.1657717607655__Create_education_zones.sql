-- Education Zone
--
-- This migration adds Education Zones which group schools and are used for school monitoring activities
CREATE TABLE education_zones (
	`id` INT(10) AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT 'ID Educational Zone',
	-- ta_id was previously geo_id
	`ta_id` INT(10) NOT NULL COMMENT 'ID Geolocation TA',
	-- district_id was previously geo2_id
	`district_id` INT(10) NULL DEFAULT NULL COMMENT 'ID Geolocation District',
    -- leaving this out from legacy mis as it's not used `geo2_id` INT(10) NULL DEFAULT NULL COMMENT 'ID Geolocation TA',
	`name` VARCHAR(100) NOT NULL COMMENT 'Name educational zone',
	-- alt_name was previously nameki which was used for translation
	`alt_name` VARCHAR(100) NULL DEFAULT NULL COMMENT 'Alternate name of educational zone',
	`code` VARCHAR(15) NOT NULL COMMENT 'Code educational zone',
	`active` TINYINT(1) NOT NULL COMMENT 'Active, 1:yes 0:no',
	`created_at` TIMESTAMP NOT NULL,
	`updated_at` TIMESTAMP NOT NULL
);