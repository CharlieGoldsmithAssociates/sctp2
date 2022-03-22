-- This migration adds the fields that make the assignments behavior behave like TransferPeriod
ALTER TABLE transfer_agencies_assignments ADD COLUMN `disbursement_frequency` tinyint(2) null comment 'When the period for this assigment starts' AFTER `transfer_method`;

ALTER TABLE transfer_agencies_assignments ADD COLUMN `period_start_date` date null comment 'When the period for this assigment starts' AFTER `disbursement_frequency`;

ALTER TABLE transfer_agencies_assignments ADD COLUMN `period_end_date` date null comment 'When the period for this assigment starts' AFTER `period_start_date`;