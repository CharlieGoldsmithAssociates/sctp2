insert into filter_templates (
    table_name,
    column_name,
    label,
    field_type,
    field_values,
    created_at,
    hint
)
values
('individuals', 'fit_for_work', 'Fit for work?', 'ListSingle', 'Yes:1\nNo:0', current_timestamp, '');