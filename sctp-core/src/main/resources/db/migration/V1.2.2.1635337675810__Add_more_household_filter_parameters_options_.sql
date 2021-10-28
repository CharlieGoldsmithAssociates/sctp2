INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'Sand' as field_text, '1' as field_value
	union select 'Mud' as field_text, '2' as field_value
	union select 'Cement' as field_text, '3' as field_value
	union select 'Tile' as field_text, '4' as field_value
	union select 'Other' as field_text, '5' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'floor_type') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'Grass' as field_text, '1' as field_value
    union select 'Paraffin' as field_text, '2' as field_value
    union select 'Electricity' as field_text, '3' as field_value
    union select 'Battery' as field_text, '4' as field_value
    union select 'Firewood' as field_text, '5' as field_value
    union select 'Candles' as field_text, '6' as field_value
    union select 'Solar' as field_text, '7' as field_value
    union select 'Gas' as field_text, '8' as field_value
    union select 'Torch' as field_text, '9' as field_value
    union select 'Other' as field_text, '10' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'fuel_source') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'Good' as field_text, '1' as field_value
    union select 'Average' as field_text, '2' as field_value
    union select 'Bad' as field_text, '3' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'house_condition') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'No toilet' as field_text, '1' as field_value
    union select 'Flush toilet' as field_text, '2' as field_value
    union select 'Vip latrine' as field_text, '3' as field_value
    union select 'Latrine with roof' as field_text, '4' as field_value
    union select 'Latrine without roof' as field_text, '5' as field_value
    union select 'Other toilet' as field_text, '6' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'latrine_type') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select '0 < 3 months' as field_text, '1' as field_value
    union select '3 - 5 months' as field_text, '2' as field_value
    union select '6 months and over' as field_text, '3' as field_value
    union select 'Did not harvest' as field_text, '4' as field_value
    union select 'Nothing in store' as field_text, '4' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'maize_harvest_lasted') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select '0 < 3 months' as field_text, '1' as field_value
    union select '3 - 5 months' as field_text, '2' as field_value
    union select '6 months and over' as field_text, '3' as field_value
    union select 'Did not harvest' as field_text, '4' as field_value
    union select 'Nothing in store' as field_text, '4' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'maize_in_granary_will_last') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select '1 meal per day' as field_text, '1' as field_value
    union select '2 meals per day' as field_text, '2' as field_value
    union select '3 meals per day' as field_text, '3' as field_value
    union select 'None' as field_text, '4' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'meals_eaten_last_week') AS template_id, vals.field_text, vals.field_value
FROM vals;


INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'Grass' as field_text, '1' as field_value
    union select 'Iron Sheets' as field_text, '2' as field_value
    union select 'Clay Tiles' as field_text, '3' as field_value
    union select 'Plastic Sheeting' as field_text, '4' as field_value
    union select 'Other' as field_text, '5' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'roof_type') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'Grass' as field_text, '1' as field_value
    union select 'Mud' as field_text, '2' as field_value
    union select 'Compacted Earth' as field_text, '3' as field_value
    union select 'Mud Brick' as field_text, '4' as field_value
    union select 'Burnt Bricks' as field_text, '5' as field_value
    union select 'Iron Sheets' as field_text, '6' as field_value
    union select 'Others' as field_text, '7' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'wall_type') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'Piped into Dwelling' as field_text, '1' as field_value
    union select 'Piped into Yard/Plot' as field_text, '2' as field_value
    union select 'Communal Standpipe' as field_text, '3' as field_value
    union select 'Open Well in Yard/Plot' as field_text, '4' as field_value
    union select 'Open Public Well' as field_text, '5' as field_value
    union select 'Protected Well in Yard/Plot' as field_text, '6' as field_value
    union select 'Protected Well' as field_text, '7' as field_value
    union select 'Borehole' as field_text, '8' as field_value
    union select 'Spring' as field_text, '9' as field_value
    union select 'River/Stream' as field_text, '10' as field_value
    union select 'Pond/Lake' as field_text, '11' as field_value
    union select 'Dam' as field_text, '12' as field_value
    union select 'Rainwater' as field_text, '13' as field_value
    union select 'Tanker Truck/Bowser' as field_text, '14' as field_value
    union select 'Other (Specify)' as field_text, '15' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'water_source') AS template_id, vals.field_text, vals.field_value
FROM vals;

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
WITH vals AS (
	select 'Poorest' as field_text, '1' as field_value
    union select 'Poorer' as field_text, '2' as field_value
    union select 'Poor' as field_text, '3' as field_value
    union select 'Better-off' as field_text, '4' as field_value
    union select 'Rich' as field_text, '5' as field_value
)
SELECT (SELECT id FROM filter_templates WHERE table_name = 'households' AND column_name = 'wealth_quintile') AS template_id, vals.field_text, vals.field_value
FROM vals;