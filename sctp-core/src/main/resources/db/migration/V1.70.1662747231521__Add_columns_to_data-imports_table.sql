alter table data_imports
    add column merge_date timestamp,
    add column ready_to_merge boolean default false
;

update data_imports set merge_date = completion_date where status = 'Merged';