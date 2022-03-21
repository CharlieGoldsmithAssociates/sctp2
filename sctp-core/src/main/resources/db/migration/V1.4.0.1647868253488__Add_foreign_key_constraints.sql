CALL dropForeignKey('district_user_profiles', 'dup_user_id_fk');
CALL dropForeignKey('district_user_profiles', 'dup_district_id_fk');

ALTER TABLE district_user_profiles
	ADD CONSTRAINT dup_user_id_fk FOREIGN KEY district_user_profiles(user_id) REFERENCES users(id),
	ADD CONSTRAINT dup_district_id_fk FOREIGN KEY district_user_profiles(district_id) REFERENCES locations(id); 