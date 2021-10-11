alter table individuals
    add relationship_to_head int comment 'Relationship code.',
    add ubr_household_member_id bigint comment 'This id is assigned from UBR';