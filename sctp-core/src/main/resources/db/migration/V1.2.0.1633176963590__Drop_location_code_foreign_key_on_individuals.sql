alter table individuals
    drop foreign key location_code_fk;

alter table individuals
    drop index location_code_fk;