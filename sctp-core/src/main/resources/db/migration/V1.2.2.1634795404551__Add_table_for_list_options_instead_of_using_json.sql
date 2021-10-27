CREATE TABLE IF NOT EXISTS filter_template_list_options (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	template_id bigint NOT NULL,
	field_text text NOT NULL,
	field_value text NOT NULL,

	CONSTRAINT opts_template_id_fk FOREIGN key(template_id) REFERENCES filter_templates(id)
) comment 'Contains list options for filter field templates';