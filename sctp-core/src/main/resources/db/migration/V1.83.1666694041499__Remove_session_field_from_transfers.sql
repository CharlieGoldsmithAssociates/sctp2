-- Removes the session field which can be referenced/reached from transfer_period
ALTER TABLE transfers DROP COLUMN transfer_session_id;