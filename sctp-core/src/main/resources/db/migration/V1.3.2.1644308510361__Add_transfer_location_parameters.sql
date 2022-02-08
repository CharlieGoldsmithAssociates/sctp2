-- This table doesn't have an equivalent in the old MIS, it is used to track amounts to be assigned per district/location
-- this is useful for transfer calculations as each district/location may have it's own amount it disburses.
CREATE TABLE `transfer_locations_parameters` (
    `id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `location_id` bigint NOT NULL COMMENT 'The location this parameter is tied to',
    `amount` BIGINT NOT NULL COMMENT 'The base amount to be allocated to this location/parameter' ,
    `active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Whether the parameter is active(1) or disabled(0)',
	`created_at` TIMESTAMP NULL DEFAULT NULL,
	`modified_at` datetime DEFAULT NULL,
    `program_id` bigint NULL COMMENT 'Optional parameter if the parameter is tied to a specific program'
);
