-- The following was a mistake on my local machine. Simply gets ignored
CALL dropForeignKey('alternate_recipient', 'hu_fk_id');

CALL dropForeignKey('alternate_recipient', 'hu_id_fk');
CALL dropIndexIfExists('alternate_recipient', 'hu_id_fk');

ALTER TABLE `alternate_recipient` ADD CONSTRAINT hu_id_fk FOREIGN KEY (`household_id` ) REFERENCES `households` (`household_id` );

CREATE UNIQUE INDEX hu_id_fk ON alternate_recipient (household_id);