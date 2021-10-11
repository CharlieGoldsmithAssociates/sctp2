alter table household_members drop foreign key household_code_fk;

alter table households drop constraint household_ml_code_unq;
alter table households drop constraint household_ubr_code_unq;