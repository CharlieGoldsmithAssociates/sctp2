DROP PROCEDURE IF EXISTS initiateTransfersForEnrolledHouseholdsInDistrict;

CREATE PROCEDURE initiateTransfersForEnrolledHouseholdsInDistrict(
  IN arg_enrollment_session_id BIGINT,
  IN arg_transfer_session_id BIGINT,
  IN arg_user_id BIGINT
)
BEGIN
  INSERT INTO transfers (
        transfer_session_id,
		program_id,
		household_id,
		receiver_id,
		enrollment_session_id,
		transfer_state,
		transfer_agency_id,
		transfer_period_start_month,
		transfer_period_start_year,
		transfer_period_end,
		transfer_period_end_year,
		district_id,
		village_cluster_id,
		traditional_authority_id,
		zone_id,
		household_member_count,
		basic_subsidy_amount,
		number_of_months,
		children_count,
		primary_children_count,
		primary_incentive_amount,
		secondary_children_count,
		secondary_incentive_amount,
		is_first_transfer,
		total_transfer_amount,
		is_suspended,
		is_withheld,
		account_number,
		amount_disbursed,
		is_collected,
		disbursement_date,
		arrears_amount,
		disbursed_by_user_id,
		is_reconciled,
		reconciliation_method,
		topup_event_id,
		topup_amount,
		created_by,
		reviewed_by,
		created_at,
		modified_at
  )
  SELECT
    arg_transfer_session_id as transfer_session_id,
	ts.program_id AS program_id,
	h.household_id,
	null as receiver_id,
	arg_enrollment_session_id AS enrollment_session_id,
	'19' as transfer_state, -- Pre-Close
	null as transfer_agency_id,
	null as transfer_period_start_month,
	null as transfer_period_start_year,
	null as transfer_period_end,
	null as transfer_period_end_year,
	l.id as district_id,
	l4.id as village_cluster_id,
	l2.id as traditional_authority_id,
	l3.id AS  zone_id,
	(SELECT count(id) FROM individuals i2 WHERE i2.household_id = h.household_id) AS household_member_count,
	0 as basic_subsidy_amount,
	0 as number_of_months,
	(select count(id) from individuals i4 WHERE i4.household_id = h.household_id and TIMESTAMPDIFF(YEAR, i4.date_of_birth, CURDATE()) >=6 and TIMESTAMPDIFF(YEAR, i4.date_of_birth, CURDATE()) <= 15 ) as children_count,
	(select count(id) from individuals i5 WHERE i5.household_id = h.household_id AND i5.highest_education_level = 2) as primary_children_count,
	0 as primary_incentive_amount,
	(select count(id) from individuals i6 WHERE i6.household_id = h.household_id AND i6.highest_education_level = 3) as secondary_children_count,
	0 as secondary_incentive_amount,
	0 AS is_first_transfer,
	0 AS total_transfer_amount,
	0 AS is_suspended,
	0 AS is_withheld,
	NULL AS account_number,
	0 AS amount_disbursed,
	0 AS is_collected,
	NULL AS disbursement_date,
	0 AS arrears_amount,
	0 AS disbursed_by_user_id,
	0 AS is_reconciled,
	NULL AS reconciliation_method,
	0 AS topup_event_id,
	0 AS topup_amount,
	arg_user_id AS created_by,
	0 AS reviewed_by,
	NOW() AS created_at,
	NOW() AS modified_at
from household_enrollment he
LEFT join households h on h.household_id = he.household_id
LEFT JOIN locations l ON l.code = h.location_code
LEFT JOIN locations l2 ON l2.code = h.ta_code
LEFT JOIN locations l3 ON l3.code = h.zone_code
LEFT JOIN locations l4 ON l4.code = h.cluster_code
LEFT JOIN locations l5 ON l5.code = h.village_code
LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
LEFT JOIN enrollment_sessions es ON es.id = he.session_id
LEFT JOIN targeting_sessions ts ON ts.id = es.target_session_id
WHERE he.session_id = arg_enrollment_session_id
AND he.status = 4; -- Status '4' is Enrolled
END