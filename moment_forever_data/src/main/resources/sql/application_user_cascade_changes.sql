ALTER TABLE application_users
ADD CONSTRAINT fk_auth_user_cascade
FOREIGN KEY (auth_user_id)
REFERENCES auth_users(id)
ON DELETE CASCADE;

ALTER TABLE application_users
RENAME COLUMN created_at TO created_on;