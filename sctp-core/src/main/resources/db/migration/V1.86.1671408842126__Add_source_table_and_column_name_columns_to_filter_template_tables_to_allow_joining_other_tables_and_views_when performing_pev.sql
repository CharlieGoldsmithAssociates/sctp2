ALTER TABLE filter_templates
    ADD source_table_name varchar(256) NULL
    COMMENT 'Source table name to be used on the right hand side when generating JOIN statements for field_types ''ForeignMappedField'' and the like';

ALTER TABLE filter_templates
    ADD source_column_name varchar(256)
    NULL COMMENT 'Source column name (prefixed with source_table_name) to be used on the right hand side when generating JOIN statements for field_types ''ForeignMappedField'' and the like';
