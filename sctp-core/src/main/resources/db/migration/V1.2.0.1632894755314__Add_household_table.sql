--
-- Household information
--

CREATE TABLE if not exists `households` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` timestamp not null,
  `modified_at` timestamp,
  `deleted_at` timestamp,
  `location_code` varchar(50) NOT NULL,
  `ml_code` varchar(50) NOT NULL,
  `ubr_code` varchar(50) NOT NULL,
  `account_number` varchar(50) NOT NULL,
  `floor_type` int NOT NULL,
  `roof_type` int NOT NULL,
  `wall_type` int NOT NULL,
  `latrine_type` int NOT NULL,
  `house_condition` int NOT NULL,
  `fuel_source` int NOT NULL,
  `has_chair` tinyint NOT NULL,
  `has_radio` tinyint NOT NULL,
  `has_bicycles` tinyint NOT NULL,
  `has_beds` tinyint NOT NULL,
  `has_mattress` tinyint NOT NULL,
  `has_sleeping_mat` tinyint NOT NULL,
  `has_blankets` tinyint NOT NULL,
  `has_water_can` tinyint NOT NULL,
  `has_kitchen_utencils` tinyint NOT NULL,
  `has_poultry` tinyint NOT NULL,
  `has_livestock` tinyint NOT NULL,
  `has_ox_cart` tinyint NOT NULL,
  `has_hoe` tinyint NOT NULL,
  `has_machete_knife` tinyint NOT NULL,
  `has_mortar` tinyint NOT NULL,
  `has_cellphone` tinyint NOT NULL,
  `has_no_assets` tinyint NOT NULL,
  `maize_harvest_lasted` int NOT NULL,
  `maize_in_granary_will_last` int NOT NULL,
  `meals_eaten_last_week` int NOT NULL,
  `has_latrine` TINYINT NOT NULL,
  `has_flush_toilet` tinyint NOT NULL,
  `has_vip_latrine` tinyint NOT NULL,
  `has_latrine_with_roof` tinyint NOT NULL,
  `has_other_toilet_type` tinyint NOT NULL,
  `receives_monetary_assistance` tinyint NOT NULL,

   survives_on_begging BOOLEAN,
   survives_on_ganyu BOOLEAN,
   survives_on_petty_trading BOOLEAN,
   survives_on_agriculture BOOLEAN,
   survives_on_other BOOLEAN,

  PRIMARY KEY (`id`),
  UNIQUE KEY `household_ml_code_unq` (`ml_code`),
  UNIQUE KEY `household_ubr_code_unq` (`ubr_code`),
  KEY `hh_location_code_fk` (`location_code`),
  CONSTRAINT `hh_location_code_fk` FOREIGN KEY (`location_code`) REFERENCES `locations` (`code`)
);