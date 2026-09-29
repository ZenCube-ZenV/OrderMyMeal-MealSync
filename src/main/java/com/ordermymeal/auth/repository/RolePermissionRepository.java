package com.ordermymeal.auth.repository;

import com.ordermymeal.auth.model.RolePermission;
import com.ordermymeal.auth.model.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, RolePermissionId> {

    List<RolePermission> findByRole_RoleId(UUID roleId);
}