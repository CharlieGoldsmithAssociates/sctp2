DROP VIEW IF EXISTS household_recipient_candidates;

CREATE VIEW household_recipient_candidates
AS
SELECT id
, CONCAT(first_name, ' ', last_name) name
, individual_id
, date_of_birth
, timestampdiff(YEAR, date_of_birth, curdate()) age
, gender
, relationship_to_head relationship
, household_id
FROM individuals i
;