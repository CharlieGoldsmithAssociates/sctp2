DROP VIEW IF EXISTS active_locations;
DROP VIEW IF EXISTS locations_info_v;

/* set the code to id for easy conversion when the column is modified */
UPDATE locations SET code = id