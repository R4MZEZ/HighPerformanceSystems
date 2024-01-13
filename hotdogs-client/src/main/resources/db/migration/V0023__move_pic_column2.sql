alter table dogs drop column profile_picture_id;
ALTER TABLE images ADD COLUMN username varchar(256) unique;