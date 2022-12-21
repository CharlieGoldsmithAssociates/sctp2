--
DROP VIEW IF EXISTS household_gvh_view;

CREATE VIEW household_gvh_view
AS
SELECT
h.group_village_head_code gvh_code
,g.name AS gvh_name
,t.code AS ta_code
,t.name AS ta_name
,count(h.household_id) AS household_count
FROM households h
JOIN location_by_codes_v g ON g.code = h.group_village_head_code 
JOIN location_by_codes_v t ON t.code = g.parentCode  
GROUP BY h.group_village_head_code
ORDER BY h.group_village_head_code
;

-- View for villages under GVH, in case cluster > zone mapping doesn't exist
DROP VIEW IF EXISTS household_gvh_villages_view;

CREATE VIEW household_gvh_villages_view
AS
SELECT
h.village_code
,v.name AS village_name
,g.code AS gvh_code
,g.name AS gvh_name
,count(h.household_id) AS household_count
FROM households h
JOIN location_by_codes_v v ON v.code = h.village_code
JOIN location_by_codes_v g ON g.code = v.parentCode AND g.location_type = 'SUBNATIONAL6'
GROUP BY h.village_code
ORDER BY h.village_code
;