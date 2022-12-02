INSERT INTO filter_templates (table_name,column_name,label,field_type,field_values,created_at,hint)
VALUES
('individuals_view','age','that are within the age bracket of','ListSingle','',current_timestamp,'')
;


INSERT INTO filter_template_list_options(template_id, field_text, field_value, operator)
WITH vals AS (
	select '0 - 2 years old' as field_text, '0,2' as field_value, 'BETWEEN' as operator
    union select '0 - 3 years old' as field_text, '0,3' as field_value, 'BETWEEN' as operator
    union select '0 - 5 years old' as field_text, '0,5' as field_value, 'BETWEEN' as operator
    union select '65 years old and above' as field_text, '65' as field_value, 'GREATER_THAN_OR_EQUAL_TO' as operator
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'individuals_view' AND column_name = 'age') AS template_id
, vals.field_text
, vals.field_value
, vals.operator
FROM vals;