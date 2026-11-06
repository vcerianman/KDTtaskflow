CREATE TABLE report (
    id              BIGSERIAL    PRIMARY KEY,
    type            VARCHAR(50),
    author          VARCHAR(120) NOT NULL,
    target_username VARCHAR(120) NOT NULL,
    reason          VARCHAR(255) NOT NULL,
    explanation     TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_report_author ON report (author);
CREATE INDEX idx_report_target_username ON report (target_username);
CREATE INDEX idx_report_type ON report (type);
CREATE INDEX idx_report_created_at ON report (created_at);
