DROP PROCEDURE IF EXISTS countEnrollmentSessionsForMobileReview;

DELIMITER $func$

CREATE PROCEDURE countEnrollmentSessionsForMobileReview(
    IN _districtCode bigint,
    IN _taCode bigint,
    IN _clusterCode bigint
)
    COMMENT 'Counts the total records that would be returned by getEnrollmentSessionsForMobileReview. This is used for paging in the API.'
BEGIN

SELECT count(es.id) totalSessions
FROM enrollment_session_v es
WHERE status = 'review' AND mobile_review = 1
AND district_code = _districtCode
AND (_taCode IS NULL OR ta_code = _taCode)
AND (_clusterCode IS NULL OR (SELECT EXISTS(SELECT 1 FROM enrollment_clusters_view ecv WHERE ecv.session_id = es.id AND ecv.code = _clusterCode)))
ORDER BY id ASC
;

END $func$

DELIMITER ;