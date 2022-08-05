-- This view lists clusters that have historically ever been part of an enrollment_session
DROP VIEW IF EXISTS enrollment_clusters_view;

CREATE VIEW enrollment_clusters_view
AS
SELECT
    l.id AS id
    ,l.code AS code
    ,l.name AS name
    ,l.location_type AS location_type
    ,es.id AS session_id
    ,es.status session_status
    ,es.mobile_review
FROM locations l
JOIN enrollment_sessions es ON find_in_set(l.code, es.cluster_code) > 0
;