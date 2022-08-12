DROP VIEW IF EXISTS location_statuses;

CREATE VIEW location_statuses AS
SELECT
l.id
,l.code
,l.name
,l.location_type
,l.parentCode parent_code
, !isnull(ts.id) as targeting
, (SELECT EXISTS(SELECT TRUE FROM targeting_results WHERE scm_user_id != NULL AND targeting_session = ts.id)) AS second_community_meeting
, (SELECT EXISTS(SELECT TRUE FROM targeting_results WHERE dm_user_id != NULL AND targeting_session = ts.id)) AS district_validation
, ts.id AS targeting_session_id
, !isnull(es.id) as enrollment
,es.id  enrollment_session_id
,!isnull(xs.id) AS transfers
,xs.id AS transfers_session_id
FROM  location_by_codes_v l
LEFT JOIN enrollment_sessions es ON (es.district_code = l.code OR es.zone_code = l.code OR es.ta_code = l.code OR FIND_IN_SET(l.code, es.cluster_code)) AND es.status = 'review'
LEFT JOIN targeting_sessions ts ON (ts.district_code = l.code OR ts.ta_code = l.code OR FIND_IN_SET(l.code, ts.clusters)) AND ts.status = 'review'
LEFT JOIN transfers_sessions xs ON  (xs.district_id = l.id AND xs.active = TRUE)
;