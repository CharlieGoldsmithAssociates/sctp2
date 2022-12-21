ALTER TABLE filter_templates
    MODIFY COLUMN field_type enum('Number','NumberSigned','Decimal','Text','Date','ListSingle','ListMultiple','NumberRange','DecimalRange', 'ForeignMappedField') NOT NULL
    COMMENT 'The type of the field. This will dictate how the field is presented/handled and validated.';
