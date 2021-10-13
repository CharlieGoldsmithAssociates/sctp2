DROP VIEW IF EXISTS active_locations;
DROP VIEW IF EXISTS locations_info_v;

ALTER TABLE locations
	MODIFY code bigint NOT NULL comment 'Will be used to store the code from the UBR';

CREATE VIEW active_locations
    AS SELECT * FROM locations WHERE active = true;

CREATE VIEW locations_info_v AS
SELECT
    `l`.`id` AS `id`,
    `l`.`code` AS `code`,
    `l`.`name` AS `name`,
    `l`.`location_type` AS `locationType`,
    `t`.`description` AS `locationDescription`,
    `l`.`latitude` AS `latitude`,
    `l`.`longitude` AS `longitude`,
    `l`.`active` AS `active`,
    `l`.`parent_id` AS `parentId`
FROM (`locations` `l` LEFT JOIN `terminologies` `t` ON ((`l`.`location_type` = `t`.`name`)));