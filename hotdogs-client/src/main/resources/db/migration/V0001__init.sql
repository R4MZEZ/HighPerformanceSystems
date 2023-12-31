create table if not exists breeds
(
    id   serial PRIMARY KEY,
    name varchar(50) not null
);

create table if not exists coordinates
(
    id        serial PRIMARY KEY,
    latitude  bigint not null,
    longitude bigint not null
);

create table if not exists owners
(
    id           serial PRIMARY KEY,
    name         varchar(50)                   not null,
    surname      varchar(50),
    is_organizer bool                          not null default false,
    coordinates  bigint references coordinates not null
);
create table if not exists users
(
    id    serial PRIMARY KEY,
    name  varchar(50)              not null,
    age   int                      not null,
    breed bigint references breeds not null,
    owner bigint references owners not null
);

create table if not exists likes
(
    id    serial PRIMARY KEY,
    sender_id   bigint references users,
    receiver_id bigint references users
);

create table if not exists matches
(
    id    serial PRIMARY KEY,
    user1_id bigint references users,
    user2_id bigint references users
);

create table if not exists interests
(
    id   serial PRIMARY KEY,
    name varchar(50) not null
);

create table if not exists users_interests
(
    id    serial PRIMARY KEY,
    user_id     bigint references users,
    interest_id bigint references interests,
    level       int not null
);



create table if not exists shows
(
    id        serial PRIMARY KEY,
    date      date                     not null,
    prize     bigint                   not null,
    winner    bigint references users,
    organizer bigint references owners not null
);

create table if not exists allowed_breeds
(
    id    serial PRIMARY KEY,
    breed_id bigint references breeds,
    show_id  bigint references shows
);