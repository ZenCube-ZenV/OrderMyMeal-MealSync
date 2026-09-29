package com.ordermymeal.organization.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Long orgId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "display_name", length = 150)
    private String displayName;

    @Column(name = "time_zone", nullable = false, length = 50)
    private String timeZone = "Asia/Kolkata";

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "next_order_number", nullable = false)
    private Long nextOrderNumber = 1L;

    @Column(name = "razorpay_key_id_encrypted")
    private String razorpayKeyIdEncrypted;

    @Column(name = "razorpay_key_secret_encrypted")
    private String razorpayKeySecretEncrypted;

    @Column(name = "razorpay_config_version", nullable = false)
    private Integer razorpayConfigVersion = 1;

    @Column(name = "webhook_path_id")
    private UUID webhookPathId;

    @Column(name = "payments_verified_at")
    private Instant paymentsVerifiedAt;

    @Column(name = "is_republish_after_cancel_allowed", nullable = false)
    private Boolean republishAfterCancelAllowed = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getNextOrderNumber() {
        return nextOrderNumber;
    }

    public void setNextOrderNumber(Long nextOrderNumber) {
        this.nextOrderNumber = nextOrderNumber;
    }

    public String getRazorpayKeyIdEncrypted() {
        return razorpayKeyIdEncrypted;
    }

    public void setRazorpayKeyIdEncrypted(String razorpayKeyIdEncrypted) {
        this.razorpayKeyIdEncrypted = razorpayKeyIdEncrypted;
    }

    public String getRazorpayKeySecretEncrypted() {
        return razorpayKeySecretEncrypted;
    }

    public void setRazorpayKeySecretEncrypted(String razorpayKeySecretEncrypted) {
        this.razorpayKeySecretEncrypted = razorpayKeySecretEncrypted;
    }

    public Integer getRazorpayConfigVersion() {
        return razorpayConfigVersion;
    }

    public void setRazorpayConfigVersion(Integer razorpayConfigVersion) {
        this.razorpayConfigVersion = razorpayConfigVersion;
    }

    public UUID getWebhookPathId() {
        return webhookPathId;
    }

    public void setWebhookPathId(UUID webhookPathId) {
        this.webhookPathId = webhookPathId;
    }

    public Instant getPaymentsVerifiedAt() {
        return paymentsVerifiedAt;
    }

    public void setPaymentsVerifiedAt(Instant paymentsVerifiedAt) {
        this.paymentsVerifiedAt = paymentsVerifiedAt;
    }

    public Boolean getRepublishAfterCancelAllowed() {
        return republishAfterCancelAllowed;
    }

    public void setRepublishAfterCancelAllowed(Boolean republishAfterCancelAllowed) {
        this.republishAfterCancelAllowed = republishAfterCancelAllowed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

