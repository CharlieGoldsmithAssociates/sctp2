--
-- The following columns contain UBR CODE (form number) and legacy household Ids (ML).
-- The idea is to continue using the same HH ID (ML) as the legacy's.
--

-- Drop constraints
alter table households
    drop constraint household_ml_code_unq,
    drop constraint household_ubr_code_unq;

-- Change the columns
alter table households
    modify ubr_code bigint not null,
    modify ml_code bigint not null;

-- Re-create the constraints
alter table households
    ADD CONSTRAINT UNIQUE household_ml_code_unq(ml_code),
    ADD CONSTRAINT UNIQUE household_ubr_code_unq(ubr_code);

alter table household_members
    ADD CONSTRAINT `household_code_fk` FOREIGN KEY (`household_code`) REFERENCES `households` (`ml_code`),
    add CONSTRAINT `hh_member_individual_fk` FOREIGN KEY (`individual_id`) REFERENCES `individuals` (`id`),
    add column ubr_member_id bigint;