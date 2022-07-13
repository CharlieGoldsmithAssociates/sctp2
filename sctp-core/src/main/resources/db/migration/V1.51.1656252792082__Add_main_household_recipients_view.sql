DROP VIEW IF EXISTS main_household_recipients;

CREATE VIEW main_household_recipients
AS
SELECT
main.id
,main.first_name
,main.last_name
,main.gender
,hr.main_photo photo
,hr.modified_at
,main.date_of_birth
,main.age
,main.individual_id
, FALSE is_alternate
, TRUE is_household_member
, hr.household_id
, h.ml_code
, main.member_code
FROM household_recipient hr
JOIN individuals_view main ON main.id = hr.main_recipient AND main.household_id = hr.household_id
JOIN households h ON h.household_id = main.household_id
;