--
-- Add pmt_score and wealth_quintile to ubr_csv_imports table
--

alter table ubr_csv_imports
    add pmt_score decimal(12, 9) comment 'PMT score: Example -0.311747601',
    add wealth_quintile int comment 'Wealth quintile parameter code';