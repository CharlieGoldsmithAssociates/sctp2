--
-- This will add 'Yes' or 'No' list options for filters that have known/static options.
--

INSERT INTO filter_template_list_options(template_id, field_text, field_value)
select id AS template_id, 'Yes' AS field_text, '1' AS field_value
from filter_templates where table_name = 'households' and column_name = 'assistance_received'
UNION ALL
select id AS template_id, 'No' AS field_text, '0' AS field_value
from filter_templates where table_name = 'households' and column_name = 'assistance_received' ORDER BY template_id ASC;