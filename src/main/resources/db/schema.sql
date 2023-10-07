drop table breeds;

create table if not exists breeds
(
    id serial PRIMARY KEY,
    name varchar(50) not null
);

create table if not exists users
(
    id serial PRIMARY KEY,
    name varchar(50) not null,
    age int not null,
    breed bigint references breeds not null,
    owner bigint references owners not null
);

create table if not exists likes
(
    sender_id bigint PRIMARY KEY references users,
    receiver_id bigint PRIMARY KEY references users
);

create table if not exists matches
(
    user1_id bigint PRIMARY KEY references users,
    user2_id bigint PRIMARY KEY references users
);

create table if not exists interests
(
    id serial PRIMARY KEY,
    name varchar(50) not null
);

create table if not exists users_interests
(
    user_id bigint PRIMARY KEY references users,
    interest_id bigint PRIMARY KEY references interests,
    level int not null
);

create table if not exists owners
(
    id serial PRIMARY KEY,
    name varchar(50) not null,
    surname varchar(50),
    is_organizer bool not null default false,
    coordinates bigint references coordinates not null
);

create table if not exists coordinates
(
    id serial PRIMARY KEY,
    latitude bigint not null,
    longitude bigint not null
);

create table if not exists shows
(
    id serial PRIMARY KEY,
    date date not null,
    prize bigint not null,
    winner bigint references users,
    organizer bigint references owners not null
);

create table if not exists shows
(
    breed_id bigint PRIMARY KEY references breeds,
    show_id bigint PRIMARY KEY references shows
);