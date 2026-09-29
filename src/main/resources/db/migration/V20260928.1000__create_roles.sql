-- =========================================================
-- OrderMyMeal - Roles
-- =========================================================

-- Required for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE roles (
    role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(30) NOT NULL,

    description VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_roles_name
        UNIQUE (name)
);

-- =========================================================
-- Default system roles
-- =========================================================

INSERT INTO roles (name, description)
VALUES
    ('superadmin', 'Platform-level administrator'),
    ('admin', 'Organization administrator'),
    ('user', 'Organization user'),
    ('vendor', 'Vendor user');

