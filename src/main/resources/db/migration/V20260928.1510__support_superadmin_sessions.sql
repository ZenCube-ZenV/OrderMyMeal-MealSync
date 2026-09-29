ALTER TABLE sessions
    ADD COLUMN user_id BIGINT;

UPDATE sessions s
SET user_id = u.user_id
FROM organization_members om
JOIN users u
    ON u.user_id = om.user_id
WHERE s.membership_id = om.membership_id;

ALTER TABLE sessions
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE sessions
    ALTER COLUMN membership_id DROP NOT NULL;

ALTER TABLE sessions
    ADD CONSTRAINT fk_session_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id);

CREATE INDEX idx_sessions_user_id
    ON sessions(user_id); 