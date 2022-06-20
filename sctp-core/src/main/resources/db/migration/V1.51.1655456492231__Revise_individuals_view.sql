drop view IF EXISTS individuals_view;

create view individuals_view
as
SELECT *
, TIMESTAMPDIFF(YEAR, date_of_birth, CURDATE()) age
, IF(ISNULL(grade_level), FALSE, TRUE) AND IF(ISNULL(NULLIF(TRIM(school_name), '')), FALSE, TRUE) in_school
FROM individuals
ORDER BY age ASC
;