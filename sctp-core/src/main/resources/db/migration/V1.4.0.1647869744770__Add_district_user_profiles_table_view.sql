DROP VIEW IF EXISTS district_user_profiles_view;

CREATE VIEW district_user_profiles_view AS
SELECT dup.*
	, u.user_name
	, CONCAT(u.first_name, ' ', u.last_name) fullname
	, CONCAT(l.name) district_name
FROM district_user_profiles dup
JOIN users u ON u.id = dup.user_id
JOIN locations l ON l.id = dup.district_id;