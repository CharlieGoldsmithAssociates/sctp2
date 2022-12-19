INSERT INTO filter_templates
(table_name, column_name, label, field_type, field_values, created_at, hint, operator, source_table_name, source_column_name)
VALUES(
    'child_headed_households_view'
    , 'household_id'
    , 'where the oldest member is not older than 18yrs old'
    , 'ForeignMappedField'
    , ''
    , current_timestamp()
    , ''
    , 'EQUALS'
    , 'households'
    , 'household_id'
);
