alter table users drop column profile_picture_id;
ALTER TABLE dogs ADD COLUMN profile_picture_id BIGINT references images;