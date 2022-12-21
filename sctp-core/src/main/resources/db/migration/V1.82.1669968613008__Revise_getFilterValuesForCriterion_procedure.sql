DROP PROCEDURE IF EXISTS getFilterValuesForCriterion;

CREATE PROCEDURE getFilterValuesForCriterion(IN _criterion_id bigint)
begin
	SELECT cf.id
		, ft.table_name tableName
		, ft.column_name columnName
		, cf.conjunction
		, cf.filter_value filterValue
		, ft.field_type fieldType
		, ft.label
		, ftlo.operator
	FROM criteria_filters cf
	JOIN filter_templates ft ON ft.id = cf.template_id
	JOIN filter_template_list_options ftlo ON ftlo.template_id = ft.id AND ftlo.field_value = cf.filter_value
	WHERE cf.criterion_id = _criterion_id
	ORDER BY cf.id ASC;
END
;