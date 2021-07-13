DROP PROCEDURE IF EXISTS lookupUsernameAndEmail;
CREATE PROCEDURE lookupUsernameAndEmail(IN _username varchar(20), IN _email varchar(50))
BEGIN
SELECT
	EXISTS (SELECT id FROM users WHERE email = _email LIMIT 1) AS emailExists,
	EXISTS (SELECT id FROM users WHERE user_name = _username LIMIT 1) AS nameExists;
END