DROP VIEW IF EXISTS transfers_v;

CREATE VIEW transfers_v
AS SELECT
    t.*,
    h.ml_code AS household_ml_code,
    h.ubr_code AS form_number,
    ta.name AS transfer_agency_name,
    hr.main_recipient AS main_recipient_id,
    COALESCE (hr.alt_recipient, hr.alt_other) alt_recipient_id,
    trim(CONCAT(IFNULL(i1.first_name, ''), ' ', IFNULL(i1.last_name, ''))) AS main_recipient_name,
    trim(CONCAT(IFNULL(althr.first_name, ''), ' ', IFNULL(althr.last_name, ''))) AS alt_recipient_name,
    hr.main_photo AS main_recipient_photo,
    hr.alt_photo AS alt_recipient_photo,
    (COALESCE(t.basic_subsidy_amount, 0.00) + COALESCE(t.secondary_bonus_amount, 0.00) + COALESCE(t.primary_bonus_amount, 0.00) + COALESCE(t.primary_incentive_amount, 0.00)) AS monthly_amount,
    (((COALESCE(t.basic_subsidy_amount, 0.00) + COALESCE(t.secondary_bonus_amount, 0.00) + COALESCE(t.primary_bonus_amount, 0.00) + COALESCE(t.primary_incentive_amount, 0.00)) * COALESCE(t.number_of_months, 0.00)) + COALESCE(t.topup_amount, 0.00)) AS total_amount_to_transfer
    FROM transfers t
    LEFT JOIN households h ON t.household_id = h.household_id
    LEFT JOIN transfer_agencies ta ON t.transfer_agency_id = ta.id
    LEFT JOIN household_recipient hr ON t.household_id = hr.household_id
    LEFT JOIN individuals i1 ON hr.main_recipient = i1.id
    LEFT JOIN alternate_household_recipients althr ON t.household_id = althr.household_id;