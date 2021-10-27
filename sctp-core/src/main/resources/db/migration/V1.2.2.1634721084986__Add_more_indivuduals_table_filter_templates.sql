insert into filter_templates (
    table_name,
    column_name,
    label,
    field_type,
    field_values,
    created_at,
    hint
)
VALUES (
'individuals'
,'disability'
,'Has this type of disability'
,'ListSingle'
,'{"options":[{"text":"Blind","value":1},{"text":"Deaf","value":2},{"text":"Speech Impairment","value":3},{"text":"Deformed Limbs","value":4},{"text":"Mentally Disabled","value":5},{"text":"Albinism","value":6},{"text":"Other","value":7},{"text":"Not Disabled","value":8}]}'
,current_timestamp
,''
),(
'individuals'
,'chronic_illness'
,'Has this kind of chronic illness'
,'ListSingle'
,'{"options":[{"text":"Chronic Malaria","value":1},{"text":"TB","value":2},{"text":"Hiv Aids","value":3},{"text":"Asthma","value":4},{"text":"Arthritis","value":5},{"text":"Epilepsy","value":6},{"text":"Cancer","value":7},{"text":"Other","value":8},{"text":"None","value":9}]}'
,current_timestamp
,''
),(
'individuals'
,'orphan_status'
,'Is orphaned'
,'ListSingle'
,'{"options":[{"text":"Single orphan","value":1},{"text":"Double orphan","value":2},{"text":"Not orphaned","value":3}]}'
,current_timestamp
,''
),(
 'individuals'
 ,'highest_education_level'
 ,'Highest education level completed'
 ,'ListSingle'
 ,'{"options":[{"text":"Nursery","value":1},{"text":"Primary","value":2},{"text":"Secondary","value":3},{"text":"Training College","value":4},{"text":"University","value":5},{"text":"Other","value":6},{"text":"None","value":7}]}'
 ,current_timestamp
 ,''
 ),(
  'individuals'
  ,'grade_level'
  ,'Has completed this school grade level'
  ,'ListSingle'
  ,'{"options":[{"text":"Standard 1","value":1},{"text":"Standard 2","value":2},{"text":"Standard 3","value":3},{"text":"Standard 4","value":4},{"text":"Standard 5","value":5},{"text":"Standard 6","value":6},{"text":"Standard 7","value":7},{"text":"Standard 8","value":8},{"text":"Form 1","value":9},{"text":"Form 2","value":10},{"text":"Form 3","value":11},{"text":"Form 4","value":12},{"text":"Other","value":13}]}'
  ,current_timestamp
  ,''
);



