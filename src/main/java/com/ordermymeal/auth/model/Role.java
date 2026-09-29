package com.ordermymeal.auth.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "name", nullable = false, length = 30, unique = true)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    protected Role() {
    }

    public UUID getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

