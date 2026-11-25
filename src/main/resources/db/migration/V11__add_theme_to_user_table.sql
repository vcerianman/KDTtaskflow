-- 1. Profile picture & introduction video on app_user
ALTER TABLE app_user
    DROP COLUMN IF EXISTS profile_picture_id,
    ADD COLUMN IF NOT EXISTS avatar_url TEXT NULL,
    ADD COLUMN IF NOT EXISTS avatar_public_id VARCHAR(255) NULL,
    ADD COLUMN IF NOT EXISTS intro_video_url TEXT NULL,
    ADD COLUMN IF NOT EXISTS intro_video_public_id VARCHAR(255) NULL;

-- 2. Up to 4 images per parent post OR reply
CREATE TABLE IF NOT EXISTS post_image (
    id            BIGSERIAL PRIMARY KEY,
    post_id       BIGINT NOT NULL REFERENCES post(id) ON DELETE CASCADE,
    image_url     TEXT NOT NULL,
    public_id     VARCHAR(255) NULL,
    display_order SMALLINT NOT NULL CHECK (display_order >= 0 AND display_order < 4),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (post_id, display_order)
);

CREATE INDEX IF NOT EXISTS idx_post_image_post_id ON post_image (post_id);