-- ============================================================
-- 001_users.sql  —  IAM: users, roles, user_roles, addresses
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Roles
CREATE TABLE roles (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(50)   NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

-- Users
CREATE TABLE users (
    id             UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    email          VARCHAR(255)  NOT NULL,
    password_hash  VARCHAR(255)  NOT NULL,
    first_name     VARCHAR(100)  NOT NULL,
    last_name      VARCHAR(100)  NOT NULL,
    phone          VARCHAR(20),
    is_active      BOOLEAN       NOT NULL DEFAULT TRUE,
    version        INTEGER       NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by     VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by     VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);

-- User Roles (join)
CREATE TABLE user_roles (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    role_id     UUID          NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT uq_user_roles UNIQUE (user_id, role_id)
);

-- Addresses
CREATE TABLE addresses (
    id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,
    label       VARCHAR(50),
    line1       VARCHAR(255)  NOT NULL,
    line2       VARCHAR(255),
    city        VARCHAR(100)  NOT NULL,
    state       VARCHAR(100)  NOT NULL,
    country     VARCHAR(100)  NOT NULL,
    pincode     VARCHAR(20)   NOT NULL,
    is_default  BOOLEAN       NOT NULL DEFAULT FALSE,
    version     INTEGER       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_by  VARCHAR(100)  NOT NULL DEFAULT 'system',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_addresses_user_id ON addresses (user_id);

-- Seed default roles
INSERT INTO roles (name, created_by, updated_by) VALUES
    ('GUEST',    'system', 'system'),
    ('CUSTOMER', 'system', 'system'),
    ('ADMIN',    'system', 'system');
