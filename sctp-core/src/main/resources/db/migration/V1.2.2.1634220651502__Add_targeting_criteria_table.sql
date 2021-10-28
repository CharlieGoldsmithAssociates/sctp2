CREATE TABLE IF NOT EXISTS targeting_criteria (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name varchar(200) NOT NULL,
	created_at timestamp NOT NULL,
	created_by bigint NOT NULL,
	active boolean NOT NULL,

	CONSTRAINT criteria_user_id_fk FOREIGN KEY (created_by) REFERENCES users(id)
) COMMENT 'This table keeps track of all targeting filters into one group, AKA criteria.';