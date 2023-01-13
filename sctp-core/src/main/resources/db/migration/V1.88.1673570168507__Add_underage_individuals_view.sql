-- where the oldest member is not older than 18 years old
DROP VIEW IF EXISTS individuals_under_18_v;

CREATE VIEW individuals_under_18_v
AS
SELECT household_id, max(age) oldest_age FROM individuals_view iv GROUP BY household_id HAVING oldest_age <= 18
;