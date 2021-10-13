DROP VIEW IF EXISTS location_by_codes_v;
CREATE VIEW location_by_codes_v
AS
SELECT child.*, parent.code parentCode
FROM active_locations parent
JOIN active_locations child ON child.parent_id = parent.id;