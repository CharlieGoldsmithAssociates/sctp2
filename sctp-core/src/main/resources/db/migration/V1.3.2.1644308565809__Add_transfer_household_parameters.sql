-- This table is somewhat similar to the `calculationoption` table in the old MIS database
CREATE TABLE `transfer_households_parameters` (
    `id` bigint PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `number_of_members` TINYINT(99) NOT NULL COMMENT 'The number of members in the household',
    `condition` ENUM('EQUALS', 'GREATER_THAN', 'GREATER_THAN_OR_EQUALS') NOT NULL COMMENT 'The condition for the parameter, ==, > or >=',
    `amount` BIGINT NOT NULL COMMENT 'The base amount to be allocated to this parameter' ,
    `active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Whether the parameter is active(1) or disabled(0)',
	`created_at` TIMESTAMP NULL DEFAULT NULL,
	`modified_at` datetime DEFAULT NULL
);
