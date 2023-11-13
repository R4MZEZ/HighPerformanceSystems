ALTER TABLE users RENAME TO dogs;
ALTER TABLE users_interactions RENAME TO dogs_interactions;

ALTER TABLE users_interests RENAME TO dogs_interests;
ALTER TABLE dogs_interests RENAME COLUMN user_id TO dog_id;

ALTER TABLE users_matches RENAME TO dogs_matches;
ALTER TABLE dogs_matches RENAME COLUMN user1_id TO dog1_id;
ALTER TABLE dogs_matches RENAME COLUMN user2_id TO dog2_id;

ALTER TABLE shows_participants RENAME COLUMN user_id TO dog_id;