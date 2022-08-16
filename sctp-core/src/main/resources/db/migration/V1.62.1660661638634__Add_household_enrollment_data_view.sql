DROP VIEW IF EXISTS household_enrollment_data;

CREATE VIEW `household_enrollment_data` AS
SELECT
    `hes`.`session_id` AS `session_id`,
    `hes`.`household_id` AS `household_id`,
    `hes`.`form_number` AS `form_number`,
    `hes`.`ml_code` AS `ml_code`,
    `hes`.`district_code` AS `district_code`,
    `hes`.`district_name` AS `district_name`,
    `hes`.`ta_code` AS `ta_code`,
    `hes`.`ta_name` AS `ta_name`,
    `hes`.`gvh_code` AS `gvh_code`,
    `hes`.`group_village_head` AS `group_village_head`,
    `hes`.`cluster_code` AS `cluster_code`,
    `hes`.`cluster_name` AS `cluster_name`,
    `hes`.`zone_code` AS `zone_code`,
    `hes`.`zone_name` AS `zone_name`,
    `hes`.`village_code` AS `village_code`,
    `hes`.`village_name` AS `village_name`,
    `hes`.`household_head` AS `household_head`,
    `hes`.`individual_id` AS `individual_id`,
    `hes`.`member_code` AS `member_code`,
    `hes`.`member_count` AS `member_count`,
    `hes`.`child_enrollment6to15` AS `child_enrollment6to15`,
    `hes`.`primary_children` AS `primary_children`,
    `hes`.`secondary_children` AS `secondary_children`,
    (
        SELECT
            COALESCE(json_arrayagg(json_object('member_id', `individuals`.`id`, 'first_name', `individuals`.`first_name`, 'last_name', `individuals`.`last_name`, 'date_of_birth', `individuals`.`date_of_birth`, 'gender', `individuals`.`gender`, 'relationship', `individuals`.`relationship_to_head`, 'household_code', `individuals`.`household_code`, 'household_id', `individuals`.`household_id`, 'individual_id', `individuals`.`individual_id`, 'member_code', `individuals`.`member_code`, 'id_issue_date', `individuals`.`id_issue_date`, 'id_expiry_date', `individuals`.`id_expiry_date`)), '[]')
        FROM
            `individuals`
        WHERE (`individuals`.`household_id` = `hes`.`household_id`)
    ) AS `household_members`,
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
    ) AS `primary_recipient`,
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
    ) AS `alternate_recipient`,
    (
        SELECT
            COALESCE(json_arrayagg(json_object('id', `se`.`id`, 'household_id', `se`.`household_id`, 'member_id', `se`.`individual_id`, 'education_level', `se`.`education_level`, 'grade', `se`.`grade`, 'school_id', `se`.`school_id`, 'status', `se`.`status`)), '[]')
        FROM
            `school_enrolled` `se`
        WHERE
            (
                `se`.`household_id` = `hes`.`household_id`
            )
    ) AS `school_enrollment`
FROM
    (
        (
            `household_enrollment_summary` `hes`
        LEFT JOIN `main_household_recipients` `mhr` ON
            (
                (
                    `mhr`.`household_id` = `hes`.`household_id`
                )
            )
        )
    LEFT JOIN `alternate_household_recipients` `ahr` ON
        (
            (
                `ahr`.`household_id` = `hes`.`household_id`
            )
        )
    );