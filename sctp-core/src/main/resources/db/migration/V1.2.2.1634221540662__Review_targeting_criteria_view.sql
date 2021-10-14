DROP VIEW IF EXISTS targeting_criteria_view;

CREATE VIEW targeting_criteria_view
AS
SELECT tc.*
	, 0 AS filters
	, CONCAT_WS(u.first_name, u.last_name) creator_name
FROM targeting_criteria tc
JOIN users u ON u.id = tc.created_by;