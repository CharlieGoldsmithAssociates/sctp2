INSERT INTO filter_templates (table_name,column_name,label,field_type,field_values,created_at,hint, operator)
VALUES
('individuals_view','age','that are exactly at the age of (=)','Number','',current_timestamp,'years', 'EQUALS')
,('individuals_view','age','that are older than the age of (>)','Number', '', current_timestamp, 'years', 'GREATER_THAN')
,('individuals_view','age','that are younger than the age of (<)','Number', '', current_timestamp, 'years', 'LESS_THAN')
,('individuals_view','age','that are at least the age of (>=)','Number', '', current_timestamp, 'years', 'GREATER_THAN_OR_EQUAL_TO')
,('individuals_view','age','that are at most the age of (<=)','Number', '', current_timestamp, 'years', 'LESS_THAN_OR_EQUAL_TO')
;
