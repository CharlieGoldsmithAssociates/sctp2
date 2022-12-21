alter table cluster_targeting DROP FOREIGN KEY targeting_session_opened_by_fk;

-- Recreate to point to the correct column
alter table cluster_targeting ADD CONSTRAINT targeting_session_opened_by_fk FOREIGN KEY (opened_by) REFERENCES users(id);