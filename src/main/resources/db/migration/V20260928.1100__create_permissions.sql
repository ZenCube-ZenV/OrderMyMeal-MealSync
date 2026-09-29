
    -- =========================================================
-- OrderMyMeal - Permissions
-- =========================================================

CREATE TABLE permissions (
    permission_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL,

    description VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_permissions_name
        UNIQUE (name)
);

-- =========================================================
-- MVP permissions
-- =========================================================

INSERT INTO permissions (name, description)
VALUES
    ('organization.view', 'View organization details'),
    ('organization.create', 'Create an organization'),
    ('organization.update', 'Update organization details'),

    ('member.view', 'View organization members'),
    ('member.create', 'Create organization members'),
    ('member.update', 'Update organization members'),
    ('member.deactivate', 'Deactivate organization members'),

    ('vendor.view', 'View vendors'),
    ('vendor.create', 'Create vendors'),
    ('vendor.update', 'Update vendor details'),
    ('vendor.deactivate', 'Deactivate vendors'),

    ('role.view', 'View roles'),
    ('permission.view', 'View permissions'),
    ('role.assign', 'Assign roles to organization members');