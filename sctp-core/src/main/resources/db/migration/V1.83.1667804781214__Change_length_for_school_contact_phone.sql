-- Alter the contact phone column for the schools since some numbers have the form XXX/YYY
ALTER TABLE schools CHANGE COLUMN contact_phone contact_phone VARCHAR(200) NOT NULL;