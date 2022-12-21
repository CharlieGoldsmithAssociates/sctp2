--
-- Adds unique key to table name, column, field type, and operator. This will allow different permutations
--
-- From this point, when adding filter_templates, the following condition must be used to obtain the appropriate template_id
--
-- SELECT (SELECT id FROM filter_templates WHERE table_name = 'tName' AND column_name = 'cName' and field_type = 'fType' and operator = 'operator') AS template_id
--
ALTER TABLE filter_templates
    DROP INDEX unique_table_and_field_name_pair;

ALTER TABLE filter_templates
    ADD CONSTRAINT filter_template_uq_key UNIQUE KEY (table_name,column_name,operator,field_type);
