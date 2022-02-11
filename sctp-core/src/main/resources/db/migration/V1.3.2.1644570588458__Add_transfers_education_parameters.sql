CREATE TABLE `transfer_education_parameters` (
	`id` INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`education_level` varchar(50) NOT NULL,
	`amount` BIGINT(19) NULL DEFAULT NULL,
	`active` TINYINT(1) NULL DEFAULT NULL,
	`created_at` TIMESTAMP NULL DEFAULT NULL,
	`modified_at` DATETIME NULL DEFAULT NULL
);