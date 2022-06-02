ALTER TABLE household_enrollment
    MODIFY status varchar(30)
;

-- Migrate the status codes
-- Some of these code cases will not be hit but are put here for full reference

-- NonRecertified(4),
-- PreEligible(6),
-- Ineligible(2),
-- Eligible(1),
-- Selected(3),
-- Enrolled(5)


UPDATE household_enrollment
SET status = CASE status
    WHEN '1' OR 1 THEN 'Eligible'
    WHEN '2' OR 2 THEN 'Ineligible'
    WHEN '3' OR 3 THEN 'Selected'
    WHEN '4' OR 4 THEN 'NonRecertified'
    WHEN '5' OR 5 THEN 'Enrolled'
    WHEN '6' OR 6 THEN 'PreEligible'
    ELSE status
END
;