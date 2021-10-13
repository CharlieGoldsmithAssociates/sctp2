DROP FUNCTION IF EXISTS getNextAutoIncrementValueForTable;

DELIMITER //

CREATE FUNCTION getNextAutoIncrementValueForTable(table_name varchar(100)) RETURNS BIGINT
READS SQL DATA
BEGIN
	DECLARE value BIGINT;

	SELECT `AUTO_INCREMENT` INTO @value
			FROM	information_schema.TABLES t
			WHERE	t.TABLE_SCHEMA  = DATABASE ()
			AND 	t.TABLE_NAME = table_name;
	RETURN @value;
END; //

DELIMITER ;