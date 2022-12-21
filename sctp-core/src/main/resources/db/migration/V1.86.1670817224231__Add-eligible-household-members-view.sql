DROP VIEW IF EXISTS eligible_household_members_view;

CREATE VIEW eligible_household_members_view
AS
SELECT i.id 
, i.household_code
, h.ubr_code form_number
, CONCAT(i.first_name, ' ', i.last_name) name 
, i.member_code
, i.date_of_birth
, i.age 
, i.gender 
, i.relationship_to_head relationship
, i.individual_id national_id
, i.disability 
, i.chronic_illness
, eh.district 
, eh.ta 
, eh.cluster 
, eh.`zone` 
, eh.village 
, eh.session_id
FROM individuals_view i
JOIN eligible_households_v eh ON eh.household_id = i.household_id
JOIN households h ON h.household_id = eh.household_id 
ORDER BY eh.household_id ASC
;
