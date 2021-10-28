DROP VIEW IF EXISTS criteria_filters_view;

CREATE VIEW criteria_filters_view
AS
SELECT cf.*,
	ft.label,
	(
	    CASE ft.table_name
	        WHEN 'individuals' THEN ft.table_name
	        WHEN 'households' THEN ft.table_name
	    ELSE '(unsupported category)'
	    END
	) AS category,
	(
		CASE ft.field_type
			WHEN 'ListSingle' THEN (
				SELECT field_text
				FROM filter_template_list_options ftlo
				WHERE ftlo.template_id = cf.template_id AND ftlo.field_value = cf.filter_value
			)
		ELSE cf.filter_value
		END
	) AS selected_value
FROM criteria_filters cf
JOIN filter_templates ft ON ft.id = cf.template_id;