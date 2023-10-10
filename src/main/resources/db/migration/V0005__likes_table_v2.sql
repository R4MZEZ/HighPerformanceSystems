ALTER TABLE likes RENAME TO user_interactions;
ALTER TABLE user_interactions ADD COLUMN is_liked bool;