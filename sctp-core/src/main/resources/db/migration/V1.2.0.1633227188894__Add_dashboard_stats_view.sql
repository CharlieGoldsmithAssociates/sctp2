DROP VIEW IF EXISTS dashboard_stats_v;

CREATE VIEW dashboard_stats_v
AS
SELECT hh.c households, i.c beneficiaries
FROM (
	SELECT count(DISTINCT household_id) c FROM households
) AS hh,
(
	SELECT count(DISTINCT individual_id) c FROM individuals
) AS i
;