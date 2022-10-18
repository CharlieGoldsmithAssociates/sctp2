-- Add column to allow multiple users to synchronize enrollment data without overwriting previously updated households
-- The MIS can then be used to overwrite the changes
alter table household_enrollment
    add column last_modified_by bigint null
    comment 'ID of user who updated this column last through the API(requests originating from the from the android APP). This column should only be updated by the API code'
;

ALTER TABLE
household_enrollment
ADD CONSTRAINT `app_user_fk` FOREIGN KEY (`last_modified_by`) REFERENCES `users` (`id`)
;