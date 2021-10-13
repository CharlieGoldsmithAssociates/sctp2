alter table households
    change id household_id bigint,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (household_id);