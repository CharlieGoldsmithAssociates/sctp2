--
-- A child headed household is a household where the oldest member is at most not older than 18 years old.
--
DROP VIEW IF EXISTS child_headed_households_view;

CREATE VIEW child_headed_households_view
AS
SELECT household_id, max(age) age
FROM individuals_view iv
GROUP BY household_id
HAVING max(age) < 18
;