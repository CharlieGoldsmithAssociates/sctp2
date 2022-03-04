DROP TABLE IF EXISTS transfers;

CREATE TABLE transfers (
	id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	program_id BIGINT NOT NULL COMMENT 'The program that the transfer will be performed under',
	household_id BIGINT NOT NULL COMMENT 'The household that should receive the transfer',
	transfer_session_id BIGINT NULL COMMENT 'The transfer session that the transfer is being done under',
	enrollment_session_id BIGINT NOT NULL COMMENT 'The enrollment the household graduated from',
	-- State information about a transfer
	transfer_state TINYINT(2) COMMENT '19 - Open: Transfer record open but not performed, 20 - Pre-Close: Final calculations done, 21 - Close: Money disbursed',
	-- Transfer agency related information
	transfer_agency_id BIGINT NULL COMMENT 'The Transfer Agency this household is assigned under for the transfer',
	transfer_period_start_month INT,
	transfer_period_start_year INT,
	transfer_period_end INT,
	transfer_period_end_year INT,
	district_id BIGINT NOT NULL COMMENT 'The district that this household was in at the time of enrollment',
	village_cluster_id BIGINT COMMENT 'For Village Cluster level transfers, the cluster',
	traditional_authority_id BIGINT,
	zone_id BIGINT,
	household_member_count INT COMMENT 'Number of household Members during enrollment',
	basic_subsidy_amount BIGINT COMMENT 'Amount to receive based on program basic amount or number of household members',
	number_of_months INT NOT NULL COMMENT 'Number of months in the transfer period',
	children_count INT COMMENT 'Total number of children',
	-- Primary school amounts / amounts
	primary_children_count INT COMMENT 'Number of children in primary school',
	primary_incentive_amount BIGINT COMMENT 'Amount to add based on number of primary going children',
	-- Secondary school params / amounts
	secondary_children_count INT COMMENT 'Number of children in secondary education',
	secondary_incentive_amount BIGINT COMMENT 'Amount to add based on number of primary going children',
	is_first_transfer TINYINT(1) DEFAULT 1 COMMENT 'Whether it is the first transfer for the household or not',
	total_transfer_amount BIGINT not null comment 'The total calculated transfer amount to be disbursed in the period',
	is_suspended TINYINT(1) COMMENT 'Whether the transfer has been Suspended for other reasons',
	is_withheld TINYINT(1) COMMENT 'Whether the transfer has been withheld because of case management issues',
	-- When transfer status is Reconciled/Closed the following fields must have valid values
	receiver_id BIGINT null COMMENT 'Receiever who will receive the funds, possible to be non-member of household',
	account_number VARCHAR(50) NULL COMMENT 'Account number assigned for transfer',
	amount_disbursed BIGINT DEFAULT 0 COMMENT 'Amount received by the household',
	is_collected TINYINT(1) DEFAULT 0 COMMENT 'Whether the amount was disbursed/delivered to the household',
	disbursement_date DATE COMMENT 'When the amount was disbursed',
	arrears_amount BIGINT null COMMENT 'Amount that is pending from this transfer',
	disbursed_by_user_id BIGINT NULL COMMENT 'User who disbursed the amount for Manual transfers',
	-- Reconciliation level fields
	is_reconciled TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Whether the transfer has been reconciled',
	reconciliation_method ENUM('Manual', 'Automated') COMMENT 'TODO: Review this field',
    -- Topup amounts for transfer, top-ups happen concurrently with transfer
	topup_event_id BIGINT COMMENT 'TODO: Review and create topup_events table which will describe why topup exists',
	topup_amount BIGINT COMMENT 'Amount to be disbursed for topup',
	-- System level stuff
	created_by BIGINT NOT NULL COMMENT 'The user who created/initiated this transfer record',
	reviewed_by BIGINT NOT NULL COMMENT 'The user who approved/reviewed the transfer record should not be == created_by',
	created_at TIMESTAMP NOT NULL,
	modified_at TIMESTAMP,
	unique KEY (program_id, household_id, enrollment_session_id)
);