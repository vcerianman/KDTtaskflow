CREATE TABLE app_user (
    id          BIGSERIAL    PRIMARY KEY,
    username    VARCHAR(80)  NOT NULL UNIQUE,
    email       VARCHAR(120) NOT NULL UNIQUE,
    full_name   VARCHAR(120),
    role        VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_user_role CHECK (
        role IN ('ADMIN', 'MANAGER', 'MEMBER')
    )
);

CREATE INDEX idx_user_username ON app_user (username);
CREATE INDEX idx_user_email ON app_user (email);
CREATE INDEX idx_user_role ON app_user (role);

-- Seed initial users
INSERT INTO app_user (username, email, full_name, role) VALUES
('KDT',   'vc@example.com',   'KDT',   'ADMIN'),
('Khanig', 'nig@example.com',   'Khang nig', 'MEMBER'),
('NgoDo',  'do@example.com',   'Ngo Lo Do',  'MEMBER');
