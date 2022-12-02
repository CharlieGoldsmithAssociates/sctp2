DROP VIEW IF EXISTS criteria_filters_view;

CREATE VIEW criteria_filters_view AS
SELECT
    cf.id AS id,
    cf.template_id AS template_id,
    cf.conjunction AS conjunction,
    cf.criterion_id AS criterion_id,
    cf.filter_value AS filter_value,
    ft.label AS label,
    (
        CASE WHEN ft.table_name IN ('individuals', 'individuals_view') THEN (SELECT 'individuals')
            WHEN ft.table_name IN ('households') THEN (SELECT 'households')
            ELSE '(unknown category)'
        END
    ) AS category,
    (
        CASE
            ft.field_type WHEN 'ListSingle' THEN (
                SELECT ftlo.field_text
                FROM filter_template_list_options ftlo
                WHERE ftlo.template_id = cf.template_id AND ftlo.field_value = cf.filter_value
            ) ELSE cf.filter_value
        END
    ) AS selected_value
FROM criteria_filters cf
JOIN filter_templates ft ON ft.id = cf.template_id;