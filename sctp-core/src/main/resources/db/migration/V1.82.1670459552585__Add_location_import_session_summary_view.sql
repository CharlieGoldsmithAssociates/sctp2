DROP VIEW IF EXISTS location_import_session_summary;

CREATE VIEW location_import_session_summary
AS
SELECT lis.start_date startDate
, lis.end_date endDate
, lis.status
, lis.imported_count locationCount
, CONCAT(u.first_name, ' ', u.last_name) createdBy
FROM location_import_session lis
JOIN users u ON u.id = lis.user_id
ORDER BY lis.id DESC
LIMIT 1;