--
-- Additional geolocation information
--

alter table households
    ADD ta_code int,
    ADD cluster_code int,
    ADD zone_code int,
    ADD village_code int after location_code;