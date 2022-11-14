-- This migration adds the 'status' column to the following views

DROP VIEW IF EXISTS household_enrollment_data;
DROP VIEW IF EXISTS household_enrollment_summary;

-- household_enrollment_summary

CREATE VIEW `household_enrollment_summary` AS
SELECT
    `he`.`session_id` AS `session_id`,
    `h`.`household_id` AS `household_id`,
    `h`.`ubr_code` AS `form_number`,
    `h`.`ml_code` AS `ml_code`,
    `he`.`status`,
    `dist`.`code` AS `district_code`,
    `dist`.`name` AS `district_name`,
    `ta`.`code` AS `ta_code`,
    `ta`.`name` AS `ta_name`,
    `gvh`.`code` AS `gvh_code`,
    `gvh`.`name` AS `group_village_head`,
    `cluster`.`code` AS `cluster_code`,
    `cluster`.`name` AS `cluster_name`,
    `zone`.`code` AS `zone_code`,
    `zone`.`name` AS `zone_name`,
    `village`.`code` AS `village_code`,
    `village`.`name` AS `village_name`,
    trim(concat(ifnull(`i`.`first_name`, ''), ' ', ifnull(`i`.`last_name`, ''))) AS `household_head`,
    upper(`i`.`individual_id`) AS `individual_id`,
    `i`.`member_code` AS `member_code`,
    (
        SELECT
            count(`individuals`.`id`)
        FROM
            `individuals`
        WHERE
            (
                `individuals`.`household_id` = `he`.`household_id`
            )
    ) AS `member_count`,
    (
        SELECT
            count(`individuals_view`.`id`)
        FROM
            `individuals_view`
        WHERE
            (
                (
                    `individuals_view`.`age` BETWEEN 6 AND 15
                )
                    AND (
                        `individuals_view`.`household_id` = `he`.`household_id`
                    )
                        AND (
                            `individuals_view`.`in_school` = TRUE
                        )
            )
    ) AS `child_enrollment6to15`,
    (
        SELECT
            count(`individuals_view`.`id`)
        FROM
            `individuals_view`
        WHERE
            (
                (
                    `individuals_view`.`household_id` = `he`.`household_id`
                )
                    AND (
                        `individuals_view`.`in_school` = TRUE
                    )
                        AND (
                            `individuals_view`.`highest_education_level` = 2
                        )
            )
    ) AS `primary_children`,
    (
        SELECT
            count(`individuals_view`.`id`)
        FROM
            `individuals_view`
        WHERE
            (
                (
                    `individuals_view`.`household_id` = `he`.`household_id`
                )
                    AND (
                        `individuals_view`.`in_school` = TRUE
                    )
                        AND (
                            `individuals_view`.`highest_education_level` = 3
                        )
            )
    ) AS `secondary_children`
FROM
    (
        (
            (
                (
                    (
                        (
                            (
                                (
                                    `household_enrollment` `he`
                                JOIN `households` `h` ON
                                    (
                                        (
                                            `h`.`household_id` = `he`.`household_id`
                                        )
                                    )
                                )
                            JOIN `locations` `dist` ON
                                (
                                    (
                                        `dist`.`code` = `h`.`location_code`
                                    )
                                )
                            )
                        JOIN `locations` `ta` ON
                            (
                                (
                                    `ta`.`code` = `h`.`ta_code`
                                )
                            )
                        )
                    JOIN `locations` `gvh` ON
                        (
                            (
                                `gvh`.`code` = `h`.`group_village_head_code`
                            )
                        )
                    )
                JOIN `locations` `cluster` ON
                    (
                        (
                            `cluster`.`code` = `h`.`cluster_code`
                        )
                    )
                )
            JOIN `locations` `zone` ON
                (
                    (
                        `zone`.`code` = `h`.`zone_code`
                    )
                )
            )
        JOIN `locations` `village` ON
            (
                (
                    `village`.`code` = `h`.`village_code`
                )
            )
        )
    LEFT JOIN `individuals` `i` ON
        (
            (
                (
                    `i`.`household_id` = `he`.`household_id`
                )
                    AND (
                        `i`.`relationship_to_head` = 1
                    )
            )
        )
    );


-- household_enrollment_data
CREATE VIEW household_enrollment_data AS
SELECT
    hes.*,
    (
        SELECT
            COALESCE(json_arrayagg(json_object('member_id', `individuals`.`id`, 'first_name', `individuals`.`first_name`, 'last_name', `individuals`.`last_name`, 'date_of_birth', `individuals`.`date_of_birth`, 'gender', `individuals`.`gender`, 'relationship', `individuals`.`relationship_to_head`, 'household_code', `individuals`.`household_code`, 'household_id', `individuals`.`household_id`, 'individual_id', `individuals`.`individual_id`, 'member_code', `individuals`.`member_code`, 'id_issue_date', `individuals`.`id_issue_date`, 'id_expiry_date', `individuals`.`id_expiry_date`)), '[]')
        FROM
            `individuals`
        WHERE
            (
                `individuals`.`household_id` = `hes`.`household_id`
            )
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
