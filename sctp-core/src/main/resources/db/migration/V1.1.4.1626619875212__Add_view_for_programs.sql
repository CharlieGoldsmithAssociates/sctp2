ALTER TABLE programs ADD COLUMN name varchar(256);

ALTER TABLE programs ADD COLUMN location bigint;
ALTER TABLE programs ADD CONSTRAINT FOREIGN KEY (location) REFERENCES locations(id);

