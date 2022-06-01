ALTER TABLE targeting_results
    ADD COLUMN old_rank int(11) comment 'Old rank if rank was changed' AFTER ranking,
    ADD COLUMN old_status varchar(30) comment 'Old status if original status was changed' AFTER old_rank,
    ADD COLUMN reason text comment 'Reason' AFTER old_status
;
