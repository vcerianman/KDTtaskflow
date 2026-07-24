
CREATE TABLE project (
                         id          BIGSERIAL    PRIMARY KEY,
                         name        VARCHAR(120) NOT NULL,
                         description TEXT,
                         status      VARCHAR(20)  NOT NULL DEFAULT 'PLANNING',
                         owner       VARCHAR(120) NOT NULL,
                         start_date  DATE,
                         due_date    DATE,
                         created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    -- CHECK constraint: chặn giá trị status không hợp lệ ngay ở tầng DB
                         CONSTRAINT chk_project_status CHECK (
                             status IN ('PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'ARCHIVED')
                             )
);

-- Index cho cột hay dùng trong WHERE (bài học index: Seq Scan -> Index Scan).
-- Thử EXPLAIN ANALYZE SELECT ... WHERE status = 'ACTIVE'; để thấy khác biệt.
CREATE INDEX idx_project_status ON project (status);