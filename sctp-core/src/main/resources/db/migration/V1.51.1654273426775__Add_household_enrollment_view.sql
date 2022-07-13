DROP VIEW IF EXISTS household_enrollment_v;

CREATE VIEW household_enrollment_v
AS
SELECT he.*
,h.ml_code
,h.ubr_code form_number
,d.name district_name
,t.name ta_name
,g.name gvh_name
,c.name cluster_name
,v.name village_name
,z.name zone_name
,(SELECT count(household_id) FROM individuals WHERE household_id = h.household_id) member_count
, CONCAT(u.first_name, ' ', u.last_name) reviewed_by
, CONCAT(i.first_name, ' ', i.last_name) household_head
FROM household_enrollment he
JOIN households h ON h.household_id  = he.household_id
JOIN locations d ON d.code = h.location_code
JOIN locations t ON t.code = h.ta_code
JOIN locations g ON g.code = h.group_village_head_code
JOIN locations c ON c.code = h.cluster_code
JOIN locations v ON v.code = h.village_code
JOIN locations z ON z.code = h.zone_code
left JOIN users u ON u.id = he.reviewer_id
LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
;