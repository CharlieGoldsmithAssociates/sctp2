--
-- Adds a view to flatten location terminology definitions
--
DROP VIEW IF EXISTS location_terminologies_v;

CREATE VIEW location_terminologies_v
AS
SELECT t0.description AS country
	 , t1.description AS subnational1
	 , t2.description AS subnational2
	 , t3.description AS subnational3
	 , t4.description AS subnational4
	 , t5.description AS subnational5
FROM terminologies t0
LEFT JOIN terminologies t1 ON t1.target_module = t0.target_module AND t1.name  = 'SUBNATIONAL1'
LEFT JOIN terminologies t2 ON t2.target_module = t0.target_module AND t2.name  = 'SUBNATIONAL2'
LEFT JOIN terminologies t3 ON t3.target_module = t0.target_module AND t3.name  = 'SUBNATIONAL3'
LEFT JOIN terminologies t4 ON t4.target_module = t0.target_module AND t4.name  = 'SUBNATIONAL4'
LEFT JOIN terminologies t5 ON t5.target_module = t0.target_module AND t5.name  = 'SUBNATIONAL5'
WHERE t0.target_module = 'Location' AND t0.name  = 'COUNTRY'
;