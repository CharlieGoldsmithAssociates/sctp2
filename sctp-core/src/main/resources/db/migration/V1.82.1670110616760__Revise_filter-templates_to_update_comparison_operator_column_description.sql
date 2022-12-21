ALTER TABLE filter_templates
MODIFY COLUMN operator enum('EQUALS','NOT_EQUALS','GREATER_THAN','LESS_THAN','GREATER_THAN_OR_EQUAL_TO','LESS_THAN_OR_EQUAL_TO')
DEFAULT 'EQUALS' NULL
COMMENT 'Operator used when building queries. Applicable for non-list types only. Other types are ignored';
