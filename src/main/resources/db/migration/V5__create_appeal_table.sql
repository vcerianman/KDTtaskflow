CREATE TABLE appeal (
    id          BIGSERIAL    PRIMARY KEY,
    username    VARCHAR(80)  NOT NULL,
    reason      TEXT         NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_appeal_username ON appeal (username);
CREATE INDEX idx_appeal_created_at ON appeal (created_at);
