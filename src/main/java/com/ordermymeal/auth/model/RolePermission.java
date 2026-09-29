package com.ordermymeal.auth.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "role_permissions", uniqueConstraints = @UniqueConstraint(name = "pk_role_permissions", columnNames = {
        "role_id", "permission_id" }))
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_role_permissions_role"))
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false, foreignKey = @ForeignKey(name = "fk_role_permissions_permission"))
    private Permission permission;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RolePermission() {
    }

    public RolePermissionId getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public Permission getPermission() {
        return permission;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}