DROP VIEW IF EXISTS transfers_v;

CREATE VIEW transfers_v
AS SELECT
    t.*,
    h.ml_code AS household_ml_code,
    h.ubr_code AS form_number,
    ta.name AS transfer_agency_name,
    (COALESCE(t.basic_subsidy_amount, 0.00) + COALESCE(t.secondary_bonus_amount, 0.00) + COALESCE(t.primary_bonus_amount, 0.00) + COALESCE(t.primary_incentive_amount, 0.00)) AS monthly_amount,
    (((COALESCE(t.basic_subsidy_amount, 0.00) + COALESCE(t.secondary_bonus_amount, 0.00) + COALESCE(t.primary_bonus_amount, 0.00) + COALESCE(t.primary_incentive_amount, 0.00)) * COALESCE(t.number_of_months, 0.00)) + COALESCE(t.topup_amount, 0.00)) AS total_amount_to_transfer,
        (
            CASE
                WHEN (
                    `mhr`.`id` IS NULL
                ) THEN (
                    SELECT
                        NULL
                )
                ELSE (
                    SELECT
                        json_object(
                            'member_id',
                            `mhr`.`id`,
                            'first_name',
                            `mhr`.`first_name`,
                            'last_name',
                            `mhr`.`last_name`,
                            'gender',
                            `mhr`.`gender`,
                            'date_of_birth',
                            `mhr`.`date_of_birth`,
                            'individual_id',
                            `mhr`.`individual_id`,
                            'household_id',
                            `mhr`.`household_id`,
                            'member_code',
                            `mhr`.`member_code`,
                            'is_household_member',
                            `mhr`.`is_household_member`
                        )
                )
            END
        ) AS `main_recipient`,
        (
            CASE
                WHEN (
                    `ahr`.`id` IS NULL
                ) THEN (
                    SELECT
                        NULL
                )
                ELSE (
                    SELECT
                        json_object(
                            'member_id',
                            `ahr`.`id`,
                            'first_name',
                            `ahr`.`first_name`,
                            'last_name',
                            `ahr`.`last_name`,
                            'gender',
                            `ahr`.`gender`,
                            'date_of_birth',
                            `ahr`.`date_of_birth`,
                            'individual_id',
                            `ahr`.`individual_id`,
                            'household_id',
                            `ahr`.`household_id`,
                            'member_code',
                            `ahr`.`member_code`,
                            'is_household_member',
                            `ahr`.`is_household_member`
                        )
                )
            END
        ) AS `secondary_recipient`,
      CONCAT(i.first_name, ' ', i.last_name) AS head_name,
      i.member_code AS head_member_code,
      i.gender AS head_gender,
      d.name AS district_name,
      tl.name AS ta_name,
      g.name AS gvh_name,
      c.name AS cluster_name,
      v.name AS village_name,
      z.name AS zone_name
    FROM transfers t
    LEFT JOIN households h ON t.household_id = h.household_id
    LEFT JOIN transfer_agencies ta ON t.transfer_agency_id = ta.id
    LEFT JOIN household_recipient hr ON t.household_id = hr.household_id
    LEFT JOIN main_household_recipients mhr ON mhr.household_id = t.household_id
    LEFT JOIN alternate_household_recipients ahr ON t.household_id = ahr.household_id
	LEFT JOIN individuals i ON i.household_id = t.household_id AND i.relationship_to_head = 1
	LEFT JOIN locations d ON d.code = h.location_code
    LEFT JOIN locations tl ON tl.code = h.ta_code
    LEFT JOIN locations g ON g.code = h.group_village_head_code
    LEFT JOIN locations c ON c.code = h.cluster_code
    LEFT JOIN locations v ON v.code = h.village_code
    LEFT JOIN locations z ON z.code = h.zone_code
	;
