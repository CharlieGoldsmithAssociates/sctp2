--
-- Additional geolocation information
--

alter table ubr_csv_imports
    ADD ta_code int,
    ADD cluster_code int,
    ADD zone_code int,
    ADD village_code int after district_code;