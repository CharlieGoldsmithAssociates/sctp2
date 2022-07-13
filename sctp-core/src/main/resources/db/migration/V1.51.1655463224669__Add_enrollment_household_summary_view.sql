DROP VIEW IF EXISTS household_enrollment_summary;

CREATE VIEW household_enrollment_summary
AS
SELECT
he.session_id
,h.household_id
, h.ubr_code form_number
, h.ml_code
, dist.code district_code
, dist.name district_name
, ta.code ta_code
, ta.name ta_name
, gvh.code gvh_code
, gvh.name group_village_head
, cluster.code cluster_code
, cluster.name cluster_name
, `zone`.code zone_code
, `zone`.name zone_name
, village.code village_code
, village.name village_name
, TRIM(CONCAT(IFNULL(i.first_name,''), ' ', IFNULL(i.last_name,''))) household_head
, UPPER(i.individual_id) individual_id
, (SELECT count(id) FROM individuals WHERE household_id = he.household_id) member_count
, (SELECT count(id) FROM individuals_view WHERE age BETWEEN 6 AND 15 AND household_id = he.household_id AND in_school = TRUE) child_enrollment6to15
, (SELECT count(id) FROM individuals_view WHERE household_id = he.household_id AND in_school = TRUE AND highest_education_level = 2) primary_children
, (SELECT count(id) FROM individuals_view WHERE household_id = he.household_id AND in_school = TRUE AND highest_education_level = 3) secondary_children
FROM household_enrollment he
JOIN households h ON h.household_id = he.household_id
JOIN locations dist ON dist.code = h.location_code
JOIN locations ta ON ta.code = h.ta_code
JOIN locations gvh ON gvh.code = h.group_village_head_code 
JOIN locations cluster ON cluster.code = h.cluster_code
JOIN locations `zone` ON `zone`.code = h.zone_code 
JOIN locations village ON village.code = h.village_code
LEFT JOIN individuals i ON i.household_id = he.household_id AND i.relationship_to_head = 1
;