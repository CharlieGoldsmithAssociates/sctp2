DROP PROCEDURE IF EXISTS countTransferPeriodsForMobile;

DELIMITER $func$

CREATE PROCEDURE countTransferPeriodsForMobile(
    IN _districtCode bigint,
    IN _taCode bigint,
    IN _clusterCode bigint
)
    COMMENT 'Counts the total records that would be returned by countTransferPeriodsForMobile. This is used for paging in the API.'
BEGIN

SELECT count(tp.id) totalPeriods
FROM transfer_periods_v tp
WHERE district_code = _districtCode
AND (_taCode IS NULL OR (FIND_IN_SET(_taCode, traditional_authority_codes)))
AND (_clusterCode IS NULL OR (FIND_IN_SET(_clusterCode, village_cluster_codes)))
ORDER BY id ASC
;

END $func$

DELIMITER ;