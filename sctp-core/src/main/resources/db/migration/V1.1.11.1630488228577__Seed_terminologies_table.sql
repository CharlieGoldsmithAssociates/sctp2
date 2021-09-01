UPDATE `terminologies` SET description = 'Country' WHERE name = 'COUNTRY';
UPDATE `terminologies` SET description = 'District' WHERE name = 'SUBNATIONAL1';
UPDATE `terminologies` SET description = 'TA' WHERE name = 'SUBNATIONAL2';
UPDATE `terminologies` SET description = 'Village Cluster' WHERE name = 'SUBNATIONAL3';
UPDATE `terminologies` SET description = 'Zone' WHERE name = 'SUBNATIONAL4';

INSERT INTO `terminologies` (name, description) values ('SUBNATIONAL5', 'Village');