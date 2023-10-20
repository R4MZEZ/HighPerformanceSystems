create table users
(
    id       serial PRIMARY KEY,
    login    varchar(30) not null unique,
    password varchar(80) not null
);

alter table dogs
    add column user_id bigint references users;
alter table owners
    add column user_id bigint references users;

create table roles
(
    id   serial primary key,
    name varchar(50) not null
);

CREATE TABLE users_roles
(
    user_id bigint not null,
    role_id int    not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
);

insert into roles (name)
values ('ROLE_DOG'),
       ('ROLE_OWNER'),
       ('ROLE_ADMIN');