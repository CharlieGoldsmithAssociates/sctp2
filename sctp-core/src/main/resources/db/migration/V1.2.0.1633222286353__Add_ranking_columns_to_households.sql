alter table households
    add cbt_rank int comment 'Community based targeting rank. Stays the same even when updates come in',
    add general_rank int comment 'General ranking as data came from UBR',
    add last_general_ranking timestamp comment 'When was the last time general occurred',
    add last_cbt_ranking timestamp comment 'When was the last time cbt occurred';