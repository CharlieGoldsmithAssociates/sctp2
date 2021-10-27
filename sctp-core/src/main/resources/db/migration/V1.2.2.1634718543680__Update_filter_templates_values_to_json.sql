update filter_templates
SET field_values = '{"options":[{"value":0,"text":"No"},{"value":1,"text":"Yes"}]}'
WHERE table_name = 'individuals' AND column_name = 'fit_for_work';