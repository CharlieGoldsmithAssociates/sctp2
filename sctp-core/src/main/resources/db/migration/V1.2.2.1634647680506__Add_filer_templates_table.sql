CREATE TABLE IF NOT EXISTS filter_templates (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	table_name varchar(100) NOT NULL comment 'The name of the table that the field references (households or individuals)',
	column_name varchar(50) NOT NULL comment 'The column name in the specified table',
	label varchar(200) comment 'Human friendly display text.',
	field_type enum ('Number', 'Decimal', 'Text', 'Date', 'ListSingle', 'ListMultiple', 'NumberRange', 'DecimalRange') NOT NULL comment 'The type of the field. This will dictate how the field is presented/handled and validated.',
	field_values varchar(2048) comment 'This field will store the fields contextual values to be presented to the user. i.e, a list might store a new line separated list of items here',
	created_at timestamp NOT NULL,

	/* Prevent the same field from being added more than once */
	CONSTRAINT unique_table_and_field_name_pair unique(table_name, column_name)
) comment 'This table contains template filters used. List items must be separated by new line character (\n)';