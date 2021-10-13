--
-- The following columns contain UBR CODE (form number) and legacy household Ids (ML).
-- The idea is to continue using the same HH ID (ML) as the legacy's.
--

-- Drop constraints
alter table household_members drop foreign key hh_member_individual_fk;
alter table household_members drop constraint household_member_uid;
ALTER TABLE household_members drop FOREIGN KEY household_code_fk;
ALTER TABLE household_members drop INDEX household_code_fk;

alter table household_members modify household_code bigint not null;