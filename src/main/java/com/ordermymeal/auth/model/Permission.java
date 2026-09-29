package com.ordermymeal.auth.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(name = "uq_permissions_name", columnNames = "name"))
public class Permission {

    @Id
    @Column(name = "permission_id", nullable = false)
    private UUID permissionId;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    protected Permission() {
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}