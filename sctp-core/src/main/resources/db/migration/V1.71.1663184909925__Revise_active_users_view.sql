drop view if exists active_users;

create view active_users
as
select * from users where status = 1;