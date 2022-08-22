DROP PROCEDURE IF EXISTS AddOrUpdateNonHouseholdAlternateRecipient;

CREATE PROCEDURE AddOrUpdateNonHouseholdAlternateRecipient
(
IN householdId bigint,
IN firstName varchar(150),
IN lastName varchar(150),
IN nationalId varchar(8),
IN nationalIdIssDate date,
IN nationalIdExpDate date,
IN _gender varchar(10),
IN dob date,
IN ts datetime,
OUT recipientId bigint)
COMMENT 'This procedure creates or updates a non-member alternate recipient and returns the primary ID of that record'
BEGIN
	INSERT INTO alternate_recipient(
	household_id
	,first_name
	 ,last_name
	 ,national_id
	 ,id_issue_date
	 ,id_expiry_date
	 ,date_of_birth
	 ,gender
	 ,created_at
	 ,modified_at
)
VALUES(
	 householdId
	 ,firstName
	 ,lastName
	 ,nationalId
	 ,nationalIdIssDate
	 ,nationalIdExpDate
	 ,dob
	 ,_gender
	 ,ts
	 ,ts
) ON DUPLICATE KEY UPDATE
	 id = LAST_INSERT_ID(id)
	 ,id_issue_date = nationalIdIssDate
	 ,id_expiry_date = nationalIdExpDate
	 ,first_name = firstName
	 ,last_name = lastName
	 ,gender = _gender
	 ,date_of_birth = dob
	 ,modified_at = ts
;

SET recipientId = (SELECT LAST_INSERT_ID());
END