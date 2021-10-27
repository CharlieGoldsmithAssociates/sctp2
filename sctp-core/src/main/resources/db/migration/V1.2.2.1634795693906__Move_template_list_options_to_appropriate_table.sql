alter table filter_templates
    modify field_values varchar(1024) comment "This field will store the field's initial contextual values to be presented to the user. For list options, refer to filter_template_list_options";

-- Move
insert into filter_template_list_options (template_id,field_value,field_text)
SELECT id AS template_id, opts.field_value, opts.field_text
FROM filter_templates ft,
JSON_TABLE(ft.field_values,
	'$.options[*]' columns(
		field_text text PATH '$.text',
		field_value text PATH '$.value'
	)
) AS opts
WHERE ft.field_type  = 'ListSingle';

-- Clear the field_values property to prevent future potential confusion
update filter_templates set field_values = '' where field_type = 'ListSingle';