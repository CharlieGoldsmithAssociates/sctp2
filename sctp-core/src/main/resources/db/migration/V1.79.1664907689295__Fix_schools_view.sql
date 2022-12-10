DROP VIEW IF EXISTS schools_v;

CREATE VIEW schools_v
AS
SELECT 
s.id AS id
, s.name
,s.code
,s.education_level AS educationLevel
,ez.name AS educationZone
,l.name AS districtName
FROM schools s
LEFT JOIN education_zones ez ON s.education_zone = ez.id 
LEFT JOIN locations l ON l.id = ez.district_id 
;
