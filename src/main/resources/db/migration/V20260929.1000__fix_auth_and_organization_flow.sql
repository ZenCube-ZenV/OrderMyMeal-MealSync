-- ============================================================
-- OrderMyMeal
-- Fix authentication/session architecture
-- ============================================================

-- SuperAdmin sessions do not have an organization membership.
ALTER TABLE sessions
ALTER COLUMN membership_id DROP NOT NULL;


-- ============================================================
-- Case-insensitive email uniqueness
-- ============================================================

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email_lower
ON users (LOWER(email));


-- ============================================================
-- Organization status values
-- ============================================================

ALTER TABLE organizations
DROP CONSTRAINT IF EXISTS organizations_status_check;

ALTER TABLE organizations
ADD CONSTRAINT organizations_status_check
CHECK (
    status IN (
        'ONBOARDING',
        'ACTIVE',
        'SUSPENDED'
    )
);


-- ============================================================
-- Existing organizations remain active
-- ============================================================

UPDATE organizations
SET status = 'ACTIVE'
WHERE status IS NULL;


-- ============================================================
-- Membership status
-- ============================================================

ALTER TABLE organization_members
DROP CONSTRAINT IF EXISTS organization_members_status_check;

ALTER TABLE organization_members
ADD CONSTRAINT organization_members_status_check
CHECK (
    status IN (
        'PENDING',
        'ACTIVE',
        'INACTIVE'
    )
); 