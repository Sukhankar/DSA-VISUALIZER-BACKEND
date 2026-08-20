-- ============================================================
-- CodeLoom - V1 Initial Schema
-- ============================================================

-- ============================================================
-- UUID SUPPORT
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- ============================================================
-- USERS
-- ============================================================

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                       email VARCHAR(255) NOT NULL,
                       username VARCHAR(50) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,

                       enabled BOOLEAN NOT NULL DEFAULT TRUE,

                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT uk_users_username UNIQUE (username),

                       CONSTRAINT chk_users_email_not_blank
                           CHECK (length(trim(email)) > 0),

                       CONSTRAINT chk_users_username_not_blank
                           CHECK (length(trim(username)) > 0)
);


-- ============================================================
-- ROLES
-- ============================================================

CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,

                       name VARCHAR(50) NOT NULL,

                       CONSTRAINT uk_roles_name UNIQUE (name),

                       CONSTRAINT chk_roles_name_not_blank
                           CHECK (length(trim(name)) > 0)
);


-- ============================================================
-- USER ROLES
-- ============================================================

CREATE TABLE user_roles (
                            user_id UUID NOT NULL,
                            role_id BIGINT NOT NULL,

                            PRIMARY KEY (user_id, role_id),

                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users (id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id)
                                    REFERENCES roles (id)
                                    ON DELETE CASCADE
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_users_email
    ON users (email);

CREATE INDEX idx_users_username
    ON users (username);

CREATE INDEX idx_user_roles_role_id
    ON user_roles (role_id);


-- ============================================================
-- INITIAL ROLES
-- ============================================================

INSERT INTO roles (name)
VALUES
    ('USER'),
    ('ADMIN');