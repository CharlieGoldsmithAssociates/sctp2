-- TARGET: individuals
-- that have this type of chronic illness (TB or HIV/AIDS or Arthritis or Epilepsy or Cancer)

DROP VIEW IF EXISTS chronically_ill_individuals_v;

CREATE VIEW  chronically_ill_individuals_v
AS
SELECT household_id, ubr_household_member_id FROM individuals WHERE chronic_illness IN (2, 3, 5, 6, 7)
;

-- TARGET: individuals
-- that have this type of disability (blind or deaf or speech impairment or deformed or mentally disabled or albinism)

DROP VIEW IF EXISTS disabled_individuals_v;

CREATE VIEW disabled_individuals_v
AS
SELECT household_id, ubr_household_member_id FROM individuals WHERE disability IN (1, 2, 3, 4, 5, 6)
;