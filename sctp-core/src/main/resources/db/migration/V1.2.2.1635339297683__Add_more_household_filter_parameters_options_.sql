-- Insert boolean parameter list options

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
select id AS template_id, 'Yes' AS field_text, '1' AS field_value
from filter_templates where table_name = 'households' and FIND_IN_SET(
	column_name,
	('labor_constrained,receives_monetary_assistance,survives_on_agriculture,survives_on_begging,survives_on_ganyu,survives_on_other,survives_on_petty_trading')
)
UNION ALL
select id AS template_id, 'No' AS field_text, '0' AS field_value
from filter_templates where table_name = 'households' and FIND_IN_SET(
	column_name,
	('labor_constrained,receives_monetary_assistance,survives_on_agriculture,survives_on_begging,survives_on_ganyu,survives_on_other,survives_on_petty_trading')
)