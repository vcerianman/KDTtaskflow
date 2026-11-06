CREATE TABLE user_vote (
    voter_id    BIGINT      NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    target_id   BIGINT      NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    vote_type   INT    NOT NULL, -- 100: LIKE, -100: DISLIKE
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (voter_id, target_id),
    CONSTRAINT chk_vote_type CHECK (vote_type >= -100 AND vote_type <= 100),
    CONSTRAINT chk_no_self_vote CHECK (voter_id <> target_id)
);

CREATE INDEX idx_user_vote_target_id ON user_vote (target_id);
CREATE INDEX idx_user_vote_voter_id ON user_vote (voter_id);
