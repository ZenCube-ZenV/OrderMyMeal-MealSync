package com.ordermymeal.auth.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class RolePermissionId implements Serializable {

    private UUID roleId;

    private UUID permissionId;

    protected RolePermissionId() {
    }

    public RolePermissionId(UUID roleId, UUID permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }
}

