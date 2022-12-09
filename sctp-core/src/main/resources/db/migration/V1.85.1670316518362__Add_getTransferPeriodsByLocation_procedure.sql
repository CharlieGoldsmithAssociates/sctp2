DROP PROCEDURE IF EXISTS getTransferPeriodsForMobile;

DELIMITER $func$

CREATE PROCEDURE getTransferPeriodsForMobile(
    IN _districtCode bigint,
    IN _taCode bigint,
    IN _clusterCode bigint,
    IN _page int,
    IN _pageSize int
)
NOT DETERMINISTIC
COMMENT 'This function returns transfer_period_view filtered by the given locations'
BEGIN

SELECT tp.*
FROM transfer_periods_v tp
WHERE district_code = _districtCode
AND (_taCode IS NULL OR (FIND_IN_SET(_taCode, traditional_authority_codes)))
AND (_clusterCode IS NULL OR (FIND_IN_SET(_clusterCode, village_cluster_codes)))
ORDER BY id ASC LIMIT _page, _pageSize
;

END $func$

DELIMITER ;