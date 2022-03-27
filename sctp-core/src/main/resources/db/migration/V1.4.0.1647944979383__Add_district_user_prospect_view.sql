DROP VIEW IF EXISTS district_user_profile_prospects_v;

CREATE VIEW district_user_profile_prospects_v
AS
SELECT u.id
,u.user_name AS userName
,concat(u.first_name,' ', u.last_name) AS fullName
FROM users u
WHERE u.`system_user` = 0 AND u.status = 1 AND u.`role` IN ('ROLE_ADMINISTRATOR', 'ROLE_STANDARD')
AND u.id NOT IN (
	SELECT user_id FROM district_user_profiles dup
);
