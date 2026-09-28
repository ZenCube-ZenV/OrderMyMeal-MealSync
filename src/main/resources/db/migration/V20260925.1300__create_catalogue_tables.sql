CREATE TABLE vendors (
    vendor_id BIGSERIAL PRIMARY KEY,
    org_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(150) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    tax_identifier VARCHAR(100),
    payout_details_encrypted BYTEA,
    encryption_key_version SMALLINT,
    lead_minutes INTEGER NOT NULL DEFAULT 900,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    deactivated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vendor_organization
        FOREIGN KEY (org_id)
        REFERENCES organizations(org_id)
);

CREATE TABLE catalogue_items (
    catalogue_item_id BIGSERIAL PRIMARY KEY,
    org_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    dietary_tag VARCHAR(50),
    photo_object_key VARCHAR(500),
    price_minor BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    gst_rate_bp INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_catalogue_item_organization
        FOREIGN KEY (org_id)
        REFERENCES organizations(org_id),

    CONSTRAINT fk_catalogue_item_vendor
        FOREIGN KEY (vendor_id)
        REFERENCES vendors(vendor_id)
);

CREATE INDEX idx_vendors_org_id ON vendors(org_id);
CREATE INDEX idx_catalogue_items_org_id ON catalogue_items(org_id);
CREATE INDEX idx_catalogue_items_vendor_id ON catalogue_items(vendor_id);
