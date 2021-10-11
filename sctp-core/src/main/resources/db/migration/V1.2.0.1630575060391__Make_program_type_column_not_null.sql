--
-- Add NOT NULL constraint to programme_type column.
--

-- Default programs to programs if program type has not been specified
UPDATE programs SET programme_type = 'PROGRAMME' WHERE programme_type IS NULL;

-- Add NOT NULL constraint
ALTER TABLE programs
    MODIFY programme_type varchar(15) NOT NULL;