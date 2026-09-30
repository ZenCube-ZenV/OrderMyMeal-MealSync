package com.ordermymeal.auth.repository;

import com.ordermymeal.auth.model.RolePermission;
import com.ordermymeal.auth.model.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, RolePermissionId> {

    List<RolePermission> findByRole_RoleId(UUID roleId);

    @Query("""
            SELECT rp
            FROM RolePermission rp
            JOIN FETCH rp.permission
            WHERE rp.role.roleId = :roleId
            """)
    List<RolePermission> findWithPermissionsByRoleId(
            @Param("roleId") UUID roleId);
}
