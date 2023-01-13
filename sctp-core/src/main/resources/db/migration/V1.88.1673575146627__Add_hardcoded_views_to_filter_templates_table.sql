INSERT INTO filter_templates (
	table_name
	, column_name 
	, label 
	, field_type 
	, field_values 
	, created_at 
	, hint 
	, operator 
	, source_table_name 
	, source_column_name 
	, target_category 
)
VALUES (
	'chronically_ill_individuals_v'
	,'household_id'
	,'that have this type of chronic illness (TB or HIV/AIDS or Arthritis or Epilepsy or Cancer)'
	,'ForeignMappedField'
	,''
	,CURRENT_TIMESTAMP()
	,''
	,'EQUALS'
	,'households'
	,'household_id'
	,'individuals'
),(
	'disabled_individuals_v'
	,'household_id'
	,'that have this type of disability (blind or deaf or speech impairment or deformed or mentally disabled or albinism)'
	,'ForeignMappedField'
	,''
	,CURRENT_TIMESTAMP()
	,''
	,'EQUALS'
	,'households'
	,'household_id'
	,'individuals'
)
;