DROP PROCEDURE IF EXISTS dropIndexIfExists;

DELIMITER $$
CREATE PROCEDURE dropIndexIfExists(IN _table_name varchar(100), IN _index_name varchar(100))
BEGIN
	IF EXISTS(
        SELECT * FROM information_schema.statistics
        WHERE TABLE_SCHEMA = DATABASE()
        AND `table_name`= _table_name
        AND `index_name` = _index_name
    ) THEN
    	SET @query = CONCAT('ALTER TABLE ', _table_name, ' DROP INDEX ', _index_name, ';');
        PREPARE stmt FROM @query;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$
DELIMITER ;