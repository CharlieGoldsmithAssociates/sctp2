CREATE TABLE IF NOT EXISTS program_users (
	program_id bigint NOT NULL,
	user_id bigint NOT NULL,
	created_at timestamp NOT NULL,
	start_date date NOT NULL,
	end_date date,
	access_level varchar(30) NOT NULL,


	FOREIGN KEY (program_id) REFERENCES programs(id),
	FOREIGN KEY (user_id) REFERENCES users(id),

	PRIMARY KEY (program_id, user_id)
);

DROP PROCEDURE IF EXISTS getProgramUserCandidates;
CREATE PROCEDURE getProgramUserCandidates(IN programId bigint)
BEGIN
	SELECT u.id userId
		,u.user_name username
		,CONCAT(u.first_name, ' ', u.last_name) fullName
	FROM users u
	WHERE u.status = 1 AND u.`system_user` = 0 AND u.id NOT IN (
		SELECT pu.user_id
		FROM program_users pu
		WHERE pu.program_id = programId
	);
END ;

DROP PROCEDURE IF EXISTS getProgramUsers;
CREATE PROCEDURE getProgramUsers(IN programId bigint)
BEGIN
	SELECT u.id userId
		, u.user_name username
		, CONCAT(u.first_name, ' ', u.last_name) fullName
		, pu.start_date startDate
		, pu.end_date endDate
		, pu.access_level accessLevel
	FROM program_users pu
	JOIN users u ON u.id = pu .user_id
	WHERE pu.program_id = programId;
END ;

DROP PROCEDURE IF EXISTS removeProgramUser;
CREATE PROCEDURE removeProgramUser(IN programId bigint, IN userId bigint)
BEGIN
	DELETE FROM program_users WHERE program_id = programId AND user_id = userId;
END ;