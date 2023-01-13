ALTER TABLE filter_templates
    ADD target_category ENUM('households','individuals') NULL COMMENT 'Determines how the filters will be listed';

UPDATE filter_templates
    SET target_category = 'households'
    WHERE table_name IN ('households', 'child_headed_households_view')
;

UPDATE filter_templates
    SET target_category = 'individuals'
    WHERE table_name IN ('individuals', 'individuals_view')
;

ALTER TABLE filter_templates
    MODIFY target_category ENUM('households','individuals') NOT NULL COMMENT 'Determines how the filters will be listed';
