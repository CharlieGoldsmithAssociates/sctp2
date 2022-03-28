create table if not exists district_user_profiles (
    id bigint primary key auto_increment,
    user_id bigint references user(id),
    district_id bigint references locations (id),
    created_at timestamp not NULL,
    active boolean not null
);