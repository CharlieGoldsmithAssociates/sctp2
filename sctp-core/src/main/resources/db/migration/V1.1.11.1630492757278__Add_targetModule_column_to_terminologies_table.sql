--
-- Adds the column target_module which will allow filtering terminology by module.
--

ALTER TABLE terminologies
    ADD COLUMN target_module varchar(50);

-- As of this migration, only location terminology is available, so the following is perfectly fine
UPDATE terminologies SET target_module = 'Location';

-- Add NOT NULL constraint
ALTER TABLE terminologies
    MODIFY target_module varchar(50) NOT NULL;