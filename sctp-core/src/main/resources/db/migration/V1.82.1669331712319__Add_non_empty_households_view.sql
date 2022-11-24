-- This view enables filtering households that do not have household members.
-- Household targeting and selection queries must use this view to filter out empty households.

drop view if exists non_empty_households_v;

create view non_empty_households_v
AS
SELECT i.household_id
, count(i.ubr_household_member_id) member_count
from individuals i
join households h on h.household_id = i.household_id
group by i.household_id
having member_count > 0
;
