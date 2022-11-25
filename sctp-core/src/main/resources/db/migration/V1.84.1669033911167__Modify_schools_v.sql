DROP VIEW IF EXISTS schools_v;

CREATE VIEW schools_v
AS
SELECT
s.id ,
s.name ,
s.code ,
s.active,
s.modified_at AS modifiedAt,
s.created_at AS createdAt,
s.education_level as educationLevel,
ez.name as educationZone,
l.name as districtName,
l.code AS districtCode
FROM schools s
LEFT JOIN education_zones ez ON s.education_zone = ez.id
LEFT JOIN locations l ON l.code  = ez.district_code;