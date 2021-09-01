DROP PROCEDURE IF EXISTS updateLocationTerminology;

CREATE PROCEDURE updateLocationTerminology (
	IN sn1 varchar(50)
  , IN sn2 varchar(50)
  , IN sn3 varchar(50)
  , IN sn4 varchar(50)
  , IN sn5 varchar(50)
)
UPDATE terminologies 
	SET description = (
		CASE name 
			WHEN 'SUBNATIONAL1' THEN sn1
			WHEN 'SUBNATIONAL2' THEN sn2
			WHEN 'SUBNATIONAL3' THEN sn3
			WHEN 'SUBNATIONAL4' THEN sn4
			WHEN 'SUBNATIONAL5' THEN sn5
		END
	)
WHERE target_module = 'Location' AND name IN ('SUBNATIONAL1','SUBNATIONAL2','SUBNATIONAL3','SUBNATIONAL4','SUBNATIONAL5');