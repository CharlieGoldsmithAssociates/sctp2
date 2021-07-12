-- Add routine to check if a column in a table exists
DROP FUNCTION IF EXISTS columnExists;

CREATE FUNCTION columnExists(tbl_name varchar(256), col_name varchar(256))
RETURNS TINYINT
DETERMINISTIC
BEGIN
    DECLARE column_exists TINYINT;

    SELECT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS WHERE
        TABLE_SCHEMA = schema() AND TABLE_NAME = tbl_name AND COLUMN_NAME = col_name
    ) INTO @column_exists;
    RETURN @column_exists;
END;
