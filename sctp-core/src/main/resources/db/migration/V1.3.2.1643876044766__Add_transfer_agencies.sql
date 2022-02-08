CREATE TABLE `transfer_agencies` (
	`id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(120) NULL DEFAULT NULL COMMENT 'Name transfer agency',
	`active` TINYINT(1) NULL DEFAULT NULL COMMENT 'Whether the agency is active in the system or not',
	`website` VARCHAR(120) NULL DEFAULT NULL COMMENT 'Website of the transfer agency',
	`address` VARCHAR(120) NULL DEFAULT NULL COMMENT 'Address of the transfer agency, either postal or physical',
	`phone` VARCHAR(20) NULL DEFAULT NULL COMMENT 'Agency reachable phone number',
	`location_id` bigint NULL COMMENT 'Location where the transfer agency is based',
	`branch` VARCHAR(120) NULL DEFAULT NULL COMMENT 'Branch name of the transfer agency',
	`representative_name` VARCHAR(120) NULL DEFAULT NULL COMMENT 'Representative name of the transfer agency',
	`representative_email` VARCHAR(120) NULL DEFAULT NULL COMMENT 'Representatives email',
	`representative_phone` VARCHAR(20) NULL DEFAULT NULL COMMENT 'Representative phone number',
	`created_at` TIMESTAMP NULL DEFAULT NULL  COMMENT 'When the agency was registered in the system',
	`modified_at` datetime DEFAULT NULL  COMMENT 'When the agency was updated in the system'
);
