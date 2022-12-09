DROP VIEW IF EXISTS transfer_periods_v;

CREATE VIEW transfer_periods_v
AS SELECT
tp.*,
CONCAT(u.first_name,' ',u.last_name) opened_by_name,
d.name as district_name,
p.name as program_name
FROM transfer_periods tp
LEFT JOIN locations d ON d.code = tp.district_code
LEFT JOIN users u ON u.id = tp.opened_by
LEFT JOIN programs p ON tp.program_id = p.id;
