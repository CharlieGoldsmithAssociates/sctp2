-- Drop foreign key constraint for program type and delivery agency
CALL dropForeignKey('programs', 'programs_ibfk_1');
CALL dropForeignKey('programs', 'programs_ibfk_2');

ALTER TABLE programs drop column program_type;
ALTER TABLE programs drop column delivery_agency;