DROP VIEW IF EXISTS alternate_household_recipients;

CREATE VIEW alternate_household_recipients
AS
SELECT
COALESCE (hr.alt_recipient, hr.alt_other) id
,COALESCE(ind_recipient.first_name, ar.first_name) first_name
,COALESCE(ind_recipient.last_name, ar.last_name) last_name
,COALESCE(ind_recipient.gender, ar.gender) gender
,hr.main_photo photo
,hr.modified_at
,COALESCE(ind_recipient.date_of_birth, ar.date_of_birth) date_of_birth
,COALESCE(ind_recipient.age, timestampdiff(YEAR, ar.date_of_birth, curdate())) age
,COALESCE(ind_recipient.individual_id, ar.national_id) individual_id
,true is_alternate
, IFNULL(hr.alt_recipient, FALSE) is_household_member
, hr.household_id
, h.ml_code
, COALESCE(ind_recipient.member_code, '') member_code
FROM household_recipient hr
JOIN households h ON h.household_id = hr.household_id
LEFT JOIN individuals_view ind_recipient ON ind_recipient.id = hr.alt_recipient AND ind_recipient.household_id = hr.household_id
LEFT JOIN alternate_recipient ar  ON ar.id = hr.alt_recipient AND ar.household_id = hr.household_id
WHERE hr.alt_recipient  IS NOT NULL OR hr.alt_other IS NOT NULL
;