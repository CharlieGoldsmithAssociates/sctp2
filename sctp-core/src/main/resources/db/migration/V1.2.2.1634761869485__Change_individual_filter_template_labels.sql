update filter_templates
    set label = 'that have this type of disability'
    where table_name = 'individuals' and column_name = 'disability';

update filter_templates
    set label = 'that have this type of chronic illness'
    where table_name = 'individuals' and column_name = 'chronic_illness';

update filter_templates
    set label = 'with orphan status of...',
    field_values = '{"options":[{"text":"Single orphan","value":1},{"text":"Double orphan","value":2},{"text":"Not orphaned","value":3}]}'
    where table_name = 'individuals' and column_name = 'orphan_status';

update filter_templates
    set label = 'that have completed this level of education'
    where table_name = 'individuals' and column_name = 'highest_education_level';

update filter_templates
    set label = 'that have attended school at this grade level'
    where table_name = 'individuals' and column_name = 'grade_level';

update filter_templates
    set label = 'that are fit for work?'
    where table_name = 'individuals' and column_name = 'fit_for_work';



