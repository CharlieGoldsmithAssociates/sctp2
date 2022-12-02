ALTER TABLE filter_template_list_options
ADD COLUMN operator enum("EQUALS", "NOT_EQUALS", "GREATER_THAN", "LESS_THAN", "GREATER_THAN_OR_EQUAL_TO", "LESS_THAN_OR_EQUAL_TO", "BETWEEN")
DEFAULT "EQUALS"
COMMENT "Operator used when building queries. Ranged operators(BETWEEN) requires `field_value` to be comma separated and without spaces";
