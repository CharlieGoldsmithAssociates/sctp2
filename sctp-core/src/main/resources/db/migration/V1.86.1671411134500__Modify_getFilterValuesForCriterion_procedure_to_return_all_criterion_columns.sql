DROP PROCEDURE IF EXISTS getFilterValuesForCriterion;

DELIMITER $$
$$
CREATE PROCEDURE getFilterValuesForCriterion(IN _criterion_id bigint)
begin
	SELECT cf.id
    , ft.table_name tableName
    , ft.column_name columnName
    , cf.conjunction
    , cf.filter_value filterValue
    , ft.field_type fieldType
    , ft.label
    , ft.source_table_name sourceTableName
    , ft.source_column_name sourceColumnName
    , (
    	CASE ft.field_type
    		WHEN 'ListSingle' THEN (
    		    SELECT operator
    		    FROM filter_template_list_options
    		    WHERE template_id = ft.id AND field_value = cf.filter_value
    		)
    		ELSE (SELECT ft.operator)
    		END
    ) operator
    FROM criteria_filters cf
    JOIN filter_templates ft ON ft.id = cf.template_id
	WHERE cf.criterion_id = _criterion_id
	ORDER BY cf.id ASC;
END$$
DELIMITER ;
