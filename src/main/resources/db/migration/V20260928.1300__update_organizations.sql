ALTER TABLE organizations
    ADD COLUMN display_name VARCHAR(150),
    ADD COLUMN time_zone VARCHAR(50) NOT NULL DEFAULT 'Asia/Kolkata',
    ADD COLUMN next_order_number BIGINT NOT NULL DEFAULT 1,
    ADD COLUMN razorpay_key_id_encrypted TEXT,
    ADD COLUMN razorpay_key_secret_encrypted TEXT,
    ADD COLUMN razorpay_config_version INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN webhook_path_id UUID,
    ADD COLUMN payments_verified_at TIMESTAMPTZ,
    ADD COLUMN is_republish_after_cancel_allowed BOOLEAN NOT NULL DEFAULT FALSE;