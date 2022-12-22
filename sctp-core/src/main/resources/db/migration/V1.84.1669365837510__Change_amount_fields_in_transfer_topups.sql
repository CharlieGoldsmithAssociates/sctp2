
ALTER TABLE transfer_topups CHANGE amount fixed_amount DECIMAL(19,6) NULL DEFAULT NULL COMMENT 'Amount to be disbursed for topup';

ALTER TABLE transfer_topups CHANGE amount_projected amount_projected DECIMAL(19,6) NULL DEFAULT NULL COMMENT 'Amount projected to be used for topups';

ALTER TABLE transfer_topups CHANGE amount_executed amount_executed DECIMAL(19,6) NULL DEFAULT NULL COMMENT 'Amount executed for the topup';
