-- This migration changes the data types of the amount fields
ALTER TABLE `transfers` CHANGE COLUMN `basic_subsidy_amount` `basic_subsidy_amount` DECIMAL(19, 6) COMMENT 'Amount to receive based on program basic amount or number of household members';
ALTER TABLE `transfers` CHANGE COLUMN `primary_incentive_amount` `primary_incentive_amount` DECIMAL(19, 6) COMMENT 'Amount to add based on number of primary going children';
ALTER TABLE `transfers` CHANGE COLUMN `total_transfer_amount` `total_transfer_amount` DECIMAL(19, 6) not null comment 'The total calculated transfer amount to be disbursed in the period';
ALTER TABLE `transfers` CHANGE COLUMN `amount_disbursed` `amount_disbursed` DECIMAL(19, 6) DEFAULT 0 COMMENT 'Amount received by the household';
ALTER TABLE `transfers` CHANGE COLUMN `arrears_amount` `arrears_amount` DECIMAL(19, 6) null COMMENT 'Amount that is pending from this transfer';
ALTER TABLE `transfers` CHANGE COLUMN `topup_amount` `topup_amount` DECIMAL(19, 6) COMMENT 'Amount to be disbursed for topup';