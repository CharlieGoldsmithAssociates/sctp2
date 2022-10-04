alter table users
    add column blocked boolean default false
    after status
;