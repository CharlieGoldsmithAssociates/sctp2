-- Removes the program_id index which was a unique index mapping program_id, household_id and enrollment_session_id
ALTER TABLE `transfers` DROP INDEX `program_id`;