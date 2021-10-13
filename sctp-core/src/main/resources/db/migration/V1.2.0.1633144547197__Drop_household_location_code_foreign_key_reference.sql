

alter table households
    drop foreign key hh_location_code_fk;

alter table households
    drop index hh_location_code_fk;