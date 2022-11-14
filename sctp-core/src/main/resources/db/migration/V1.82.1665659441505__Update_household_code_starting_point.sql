-- Update ML code id to prevent conflicts with existing households
update id_counters set counter = 5000000 where name = 'household_id';