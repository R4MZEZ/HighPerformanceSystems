create table if not exists images
(
    id           serial PRIMARY KEY,
    content_type varchar(256) not null,
    data         oid          not null,
    size         bigint       not null
);

ALTER TABLE users ADD COLUMN profile_picture_id BIGINT references images;