INSERT INTO filter_templates (table_name,column_name,label,field_type,field_values,created_at,hint)
VALUES
('individuals_view','relationship_to_head','that have a relationship status of','ListSingle','',current_timestamp,'')
;


INSERT INTO filter_template_list_options(template_id, field_text, field_value, operator)
WITH vals AS (
	select 'Head' as field_text, '1' as field_value, 'EQUALS' as operator
    union select 'Spouse' as field_text, '2' as field_value, 'EQUALS' as operator
    union select 'Own Child' as field_text, '3' as field_value, 'EQUALS' as operator
    union select 'Brother or Sister' as field_text, '4' as field_value, 'EQUALS' as operator
    union select 'Grandchild' as field_text, '5' as field_value, 'EQUALS' as operator
    union select 'Parent' as field_text, '6' as field_value, 'EQUALS' as operator
    union select 'Other Relative' as field_text, '7' as field_value, 'EQUALS' as operator
    union select 'Not Related' as field_text, '8' as field_value, 'EQUALS' as operator
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'individuals_view' AND column_name = 'relationship_to_head') AS template_id
, vals.field_text
, vals.field_value
, vals.operator
FROM vals;