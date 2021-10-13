-- household_members definition

CREATE TABLE IF NOT EXISTS `household_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `individual_id` bigint NOT NULL,
  `household_code` varchar(50) NOT NULL,
  `relationship_to_head` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `household_member_uid` (`individual_id`,`household_code`) COMMENT 'Assumes a member belongs to only one household',
  KEY `household_code_fk` (`household_code`),
  CONSTRAINT `hh_member_individual_fk` FOREIGN KEY (`individual_id`) REFERENCES `individuals` (`id`),
  CONSTRAINT `household_code_fk` FOREIGN KEY (`household_code`) REFERENCES `households` (`ml_code`)
);