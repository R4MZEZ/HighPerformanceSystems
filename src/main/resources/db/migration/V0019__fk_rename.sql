ALTER TABLE dogs RENAME CONSTRAINT "users_breed_fkey" TO "dogs_breed_fkey";
ALTER TABLE dogs RENAME CONSTRAINT "users_cur_recommended_fkey" TO "dogs_cur_recommended_fkey";
ALTER TABLE dogs RENAME CONSTRAINT "users_owner_fkey" TO "dogs_owner_fkey";
ALTER TABLE dogs_interactions RENAME CONSTRAINT "likes_receiver_id_fkey" TO "dogs_interactions_receiver_id_fkey";
ALTER TABLE dogs_interactions RENAME CONSTRAINT "likes_sender_id_fkey" TO "dogs_interactions_sender_id_fkey";
ALTER TABLE dogs_interests RENAME CONSTRAINT "users_interests_interest_id_fkey" TO "dogs_interests_interest_id_fkey";
ALTER TABLE dogs_interests RENAME CONSTRAINT "users_interests_user_id_fkey" TO "dogs_interests_user_id_fkey";
ALTER TABLE dogs_matches RENAME CONSTRAINT "matches_user1_id_fkey" TO "matches_dog1_id_fkey";
ALTER TABLE dogs_matches RENAME CONSTRAINT "matches_user2_id_fkey" TO "matches_dog2_id_fkey";