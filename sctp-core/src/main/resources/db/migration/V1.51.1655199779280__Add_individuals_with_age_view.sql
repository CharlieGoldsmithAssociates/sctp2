DROP VIEW IF EXISTS individuals_view;

CREATE VIEW individuals_view
AS
SELECT *,
TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) age
FROM individuals
ORDER BY age ASC
;