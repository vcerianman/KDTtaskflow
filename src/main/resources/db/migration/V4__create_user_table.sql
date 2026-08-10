CREATE TABLE app_user (
    id          BIGSERIAL    PRIMARY KEY,
    username    VARCHAR(80)  NOT NULL UNIQUE,
    email       VARCHAR(120) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
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

-- Seed initial users with default password 'password123'
INSERT INTO app_user (username, email, password, full_name, role) VALUES
('KDT',    'vc@example.com',  '$2a$10$BVgX4RNvN0VcLaKmoLm8KuIOZSJ.9TCHf655YuHmXta/FzK7mUQXC', 'KDT',       'ADMIN'),
('Khanig', 'nig@example.com', '$2a$10$BVgX4RNvN0VcLaKmoLm8KuIOZSJ.9TCHf655YuHmXta/FzK7mUQXC', 'Khang nig', 'MEMBER'),
('NgoDo',  'do@example.com',  '$2a$10$BVgX4RNvN0VcLaKmoLm8KuIOZSJ.9TCHf655YuHmXta/FzK7mUQXC', 'Ngo Lo Do',  'MEMBER');
