DROP VIEW IF EXISTS education_zones_v;

CREATE VIEW education_zones_v
AS
SELECT
  eduzone.id AS id,
  ta.name AS taName ,
  district.name as districtName,
  eduzone.ta_code AS taCode,
  eduzone.district_code AS districtCode,
  eduzone.name,
  eduzone.alt_name AS altName,
  eduzone.code,
  eduzone.active,
  eduzone.created_at AS createdAt,
  eduzone.updated_at AS updatedAt
FROM education_zones eduzone
LEFT JOIN locations district ON district.code = eduzone.district_code
LEFT JOIN locations ta ON ta.code = eduzone.ta_code