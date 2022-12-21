-- District level household listing

DROP VIEW IF EXISTS household_districts;
CREATE VIEW household_districts_view
AS
SELECT location_code district_code, l.name district_name, count(household_id) household_count
FROM households h
JOIN location_by_codes_v l ON l.code  = h.location_code
GROUP BY h.location_code
ORDER BY district_code ASC;

-- Traditional authority level household listing

DROP VIEW IF EXISTS household_traditional_authorities_view;
CREATE VIEW household_traditional_authorities_view
AS
SELECT h.ta_code, l.name ta_name, d.code district_code, d.name district_name, count(household_id) household_count
FROM households h
JOIN location_by_codes_v l ON l.code = h.ta_code
JOIN location_by_codes_v d ON d.code = l.parentCode
GROUP BY h.ta_code
ORDER BY h.ta_code ASC;

-- Cluster level household listing
DROP VIEW IF EXISTS household_clusters_view;
CREATE VIEW household_clusters_view
AS
SELECT h.cluster_code, c.name cluster_name, t.code ta_code, t.name ta_name, count(household_id) household_count
FROM households h
JOIN location_by_codes_v c ON c.code = h.cluster_code
JOIN location_by_codes_v t ON t.code = c.parentCode
GROUP BY h.cluster_code
ORDER BY h.cluster_code ASC;

-- Zone level household listing
DROP VIEW IF EXISTS household_zones_view;
CREATE VIEW household_zones_view
AS
SELECT h.zone_code, z.name zone_name, c.code cluster_code, c.name cluster_name, count(household_id) household_count
FROM households h
JOIN location_by_codes_v z ON z.code = h.zone_code
JOIN location_by_codes_v c ON c.code = z.parentCode
GROUP BY h.zone_code
ORDER BY h.zone_code ASC;

-- Village level household listing
DROP VIEW IF EXISTS household_villages_view;
CREATE VIEW household_villages_view
AS
SELECT h.village_code, v.name village_name, z.code zone_code, z.name zone_name, count(household_id) household_count
FROM households h
JOIN location_by_codes_v v ON v.code = h.village_code
JOIN location_by_codes_v z ON z.code = v.parentCode
GROUP BY h.village_code
ORDER BY h.village_code ASC;
