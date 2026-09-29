-- =========================================================
-- OrderMyMeal - Role Permissions
-- =========================================================

CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_role_permissions
        PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles(role_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(permission_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_permission_id
    ON role_permissions(permission_id);


-- =========================================================
-- Default permissions for Super Admin
-- =========================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'superadmin';


-- =========================================================
-- Default permissions for Organization Admin
-- =========================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'organization.view',
        'organization.update',

        'member.view',
        'member.create',
        'member.update',
        'member.deactivate',

        'vendor.view',
        'vendor.create',
        'vendor.update',
        'vendor.deactivate',

        'role.view',
        'permission.view',
        'role.assign'
    )
WHERE r.name = 'admin';


-- =========================================================
-- Default permissions for Vendor
-- =========================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'vendor.view',
        'vendor.update'
    )
WHERE r.name = 'vendor';


-- =========================================================
-- Default permissions for User
-- =========================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
JOIN permissions p
    ON p.name IN (
        'organization.view',
        'member.view'
    )
WHERE r.name = 'user';


-- =========================================================
-- Update Membership Roles
-- Replace role text with role_id reference
-- =========================================================

ALTER TABLE membership_roles
ADD COLUMN role_id UUID;


-- Map existing role names to the Roles master table
UPDATE membership_roles mr
SET role_id = r.role_id
FROM roles r
WHERE mr.role = r.name;


-- Make sure every existing membership role was mapped
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM membership_roles
        WHERE role_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'Some membership_roles records could not be mapped to roles';
    END IF;
END $$;


ALTER TABLE membership_roles
ALTER COLUMN role_id SET NOT NULL;


-- Add relationship to Roles
ALTER TABLE membership_roles
ADD CONSTRAINT fk_membership_roles_role
FOREIGN KEY (role_id)
REFERENCES roles(role_id);


-- Prevent duplicate role assignment
ALTER TABLE membership_roles
ADD CONSTRAINT uq_membership_roles_membership_role_id
UNIQUE (membership_id, role_id);


-- Remove old text-based role
ALTER TABLE membership_roles
DROP COLUMN role;


-- Index for role-based lookups
CREATE INDEX idx_membership_roles_role_id
    ON membership_roles(role_id);