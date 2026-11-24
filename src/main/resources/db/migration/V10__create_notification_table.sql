CREATE TABLE notifications (
    id           BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    actor_id     BIGINT REFERENCES app_user(id) ON DELETE SET NULL,
    type         VARCHAR(50) NOT NULL,
    message      TEXT NOT NULL,
    link         VARCHAR(255),
    is_read      BOOLEAN NOT NULL DEFAULT FALSE,
    is_seen      BOOLEAN NOT NULL DEFAULT FALSE,
    seen_at      TIMESTAMPTZ NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_recipient
    ON notifications (recipient_id, is_read, created_at DESC);

CREATE INDEX idx_notifications_seen_cleanup
    ON notifications (is_seen, seen_at)
    WHERE is_seen = TRUE;

CREATE INDEX idx_notifications_dedup
    ON notifications (recipient_id, type, actor_id);
