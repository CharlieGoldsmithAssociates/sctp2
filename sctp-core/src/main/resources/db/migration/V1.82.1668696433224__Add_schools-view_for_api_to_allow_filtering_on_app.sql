drop view if exists schools_view;

create view schools_view as
select
s.id
,s.name school_name
,s.code school_code
,s.education_level
,s.active
,ez.id ez_id
,ez.code ez_code
,ez.name ez_name
,ez.district_code  ez_district_code
,d.name ez_district_name
,ez.ta_code ez_ta_code
,t.name ez_ta_name
from schools s
join education_zones ez on ez.id = s.education_zone
join locations d on d.code = ez.district_code
join locations t on t.code = ez.ta_code
;
