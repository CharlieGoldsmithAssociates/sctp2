-- Update heads first
UPDATE individuals i
SET member_code = CONCAT(household_code, '-1')
WHERE member_code IS NULL AND relationship_to_head = 1
;

-- update the rest, starting at 1 since 1 is already assigned above
WITH cte AS (
	SELECT id, household_code, ROW_NUMBER() OVER(PARTITION BY household_code) + 1 AS rn
	FROM individuals i
	WHERE member_code IS NULL AND relationship_to_head != 1
)
UPDATE individuals i1
join cte ON cte.id = i1.id
SET member_code = CONCAT(i1.household_code , '-', cte.rn)
WHERE member_code IS NULL AND relationship_to_head != 1
;