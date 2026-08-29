CREATE TABLE app_user (
    id          BIGSERIAL    PRIMARY KEY,
    username    VARCHAR(80)  NOT NULL UNIQUE,
    email       VARCHAR(120) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(120),
    about       TEXT[],
    projects    TEXT[],
    role        VARCHAR(20)  NOT NULL DEFAULT 'MEMBER',
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_user_role CHECK (
        role IN ('GOD', 'ADMIN', 'MANAGER', 'MEMBER')
    ),
    CONSTRAINT chk_user_status CHECK (
        status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')
    )
);

CREATE INDEX idx_user_username ON app_user (username);
CREATE INDEX idx_user_email ON app_user (email);
CREATE INDEX idx_user_role ON app_user (role);
CREATE INDEX idx_user_status ON app_user (status);
CREATE INDEX idx_user_deleted ON app_user (deleted);

-- Seed initial users with default password 'password123'
INSERT INTO app_user (username, email, password, full_name, about, projects, role, status, deleted) VALUES
('KDT',    'vc@example.com',  '$2a$10$BVgX4RNvN0VcLaKmoLm8KuIOZSJ.9TCHf655YuHmXta/FzK7mUQXC', 'KDT',       ARRAY['Fullstack React & Spring Boot Engineering', 'Lead Architect & System Administrator', '10+ years in software engineering'], ARRAY['TaskFlow Capstone', 'B2B Customer Portal', 'HRM-CTEL'], 'GOD',    'ACTIVE', false),
('Khanig', 'nig@example.com', '$2a$10$BVgX4RNvN0VcLaKmoLm8KuIOZSJ.9TCHf655YuHmXta/FzK7mUQXC', 'Khang nig', ARRAY['Backend Architecture', 'Payment Integrations', 'Payment Systems Engineer'],                                            ARRAY['Project A', 'TaskFlow Capstone'],                         'MEMBER', 'ACTIVE', false),
('NgoDo',  'do@example.com',  '$2a$10$BVgX4RNvN0VcLaKmoLm8KuIOZSJ.9TCHf655YuHmXta/FzK7mUQXC', 'Ngo Lo Do',  ARRAY['Database optimization', 'Frontend integration', 'Active member'],                                                     ARRAY['TaskFlow Capstone'],                                       'MEMBER', 'ACTIVE', false);
