alter table households
    add cbt_selection boolean comment 'Whether this household has been selected in a community based targeting',
    add cbt_session_id bigint comment 'The session that the selection is under. Will be overwritten periodically.',
    add cbt_pmt decimal(12, 9) comment 'The pmt score at the time of ranking',
    add cbt_status int comment '1 - Qualified, 0 - Not qualified';