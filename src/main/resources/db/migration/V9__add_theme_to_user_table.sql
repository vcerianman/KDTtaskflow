ALTER TABLE app_user ADD COLUMN IF NOT EXISTS theme VARCHAR(50) NOT NULL DEFAULT 'light';
UPDATE app_user SET theme = 'light' WHERE theme IS NULL;
