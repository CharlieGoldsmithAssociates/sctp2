-- Remove NOT NULL Constraint

ALTER TABLE alternate_recipient MODIFY COLUMN id_issue_date date;

ALTER TABLE alternate_recipient MODIFY COLUMN id_expiry_date date;