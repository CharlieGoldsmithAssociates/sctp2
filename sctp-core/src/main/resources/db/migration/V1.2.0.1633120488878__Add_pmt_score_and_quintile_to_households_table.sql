--
-- Add pmt_score and wealth_quintile to households table
--

alter table households
    add pmt_score decimal(12, 9) comment 'PMT score: Example -0.311747601',
    add wealth_quintile int comment 'Wealth quintile parameter code';