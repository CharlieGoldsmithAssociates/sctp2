drop VIEW if exists assigned_agencies_view;

create view assigned_agencies_view
as
SELECT
 taa.id AS id,
 taa.program_id AS program_id,
 taa.transfer_agency_id AS transfer_agency_id,
 taa.location_id AS location_id,
 taa.transfer_method AS transfer_method,
 p.name AS programme_name,
 p.start_date AS program_start_date,
 loc.name AS location_name,
 loc.code AS location_code,
 p.active AS is_program_active,
 loc.active AS is_location_active,
 ta.active AS is_agency_active,
 taa.created_at AS assigned_on
FROM
transfer_agencies_assignments taa
INNER JOIN programs p ON p.id = taa.program_id
INNER JOIN transfer_agencies ta ON ta.id = taa.transfer_agency_id
INNER JOIN locations loc ON loc.id = taa.location_id