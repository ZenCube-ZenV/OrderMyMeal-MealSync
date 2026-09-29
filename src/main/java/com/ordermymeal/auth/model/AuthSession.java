package com.ordermymeal.auth.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class AuthSession {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "membership_id")
    private Long membershipId;

    @Column(name = "token_hash", nullable = false, unique = true, columnDefinition = "bytea")
    private byte[] tokenHash;

    @Column(name = "last_used_at", nullable = false)
    private Instant lastUsedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthSession() {
    }

    public AuthSession(
            UUID id,
            Long userId,
            Long membershipId,
            byte[] tokenHash,
            Instant lastUsedAt,
            Instant expiresAt,
            Instant revokedAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.membershipId = membershipId;
        this.tokenHash = tokenHash;
        this.lastUsedAt = lastUsedAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public byte[] getTokenHash() {
        return tokenHash;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void revoke() {
        this.revokedAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}