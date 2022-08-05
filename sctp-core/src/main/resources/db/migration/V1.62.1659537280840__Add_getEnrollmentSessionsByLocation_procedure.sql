DROP PROCEDURE IF EXISTS getEnrollmentSessionsForMobileReview;

DELIMITER $func$

CREATE PROCEDURE getEnrollmentSessionsForMobileReview(
    IN _districtCode bigint,
    IN _taCode bigint,
    IN _clusterCode bigint,
    IN _page int,
    IN _pageSize int
)
NOT DETERMINISTIC
COMMENT 'This function returns enrollment_sessions_view filtered by the given locations'
BEGIN

SELECT es.*
FROM enrollment_session_v es
WHERE status = 'review' AND mobile_review = 1
AND district_code = _districtCode
AND (_taCode IS NULL OR ta_code = _taCode)
AND (_clusterCode IS NULL OR (SELECT EXISTS(SELECT 1 FROM enrollment_clusters_view ecv WHERE ecv.session_id = es.id AND ecv.code = _clusterCode)))
ORDER BY id ASC LIMIT _page, _pageSize
;

END $func$

DELIMITER ;