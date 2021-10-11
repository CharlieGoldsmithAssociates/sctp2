--
-- Convert to snake case
--

alter table ubr_csv_imports
    change firstName first_name varchar(50),
    change lastName last_name varchar(50);