CREATE TABLE post (
    id          BIGSERIAL    PRIMARY KEY,
    title       VARCHAR(255),
    description TEXT         NOT NULL,
    author      VARCHAR(120) NOT NULL,
    reply       BIGINT       REFERENCES post(id) ON DELETE CASCADE,
    project_id  BIGINT       REFERENCES project(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_post_project_id ON post (project_id);
CREATE INDEX idx_post_reply ON post (reply);
CREATE INDEX idx_post_author ON post (author);
CREATE INDEX idx_post_created_at ON post (created_at);
