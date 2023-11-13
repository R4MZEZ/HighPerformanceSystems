create table if not exists shows_participants
(
    id    serial PRIMARY KEY,
    show_id   bigint references shows,
    user_id bigint references users
);