package com.ordermymeal.auth.service;

import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.auth.model.Role;
import com.ordermymeal.auth.model.RolePermission;
import com.ordermymeal.auth.model.UserRole;
import com.ordermymeal.auth.repository.RolePermissionRepository;
import com.ordermymeal.auth.repository.UserRoleRepository;
import com.ordermymeal.membership.model.MembershipRole;
import com.ordermymeal.membership.repository.MembershipRoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorizationService {

    private final MembershipRoleRepository membershipRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    public AuthorizationService(
            MembershipRoleRepository membershipRoleRepository,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository) {

        this.membershipRoleRepository = membershipRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    // ==============================
    // ORGANIZATION-LEVEL AUTHORIZATION
    // ==============================

    public Set<Role> getRoles(Long membershipId) {

        return membershipRoleRepository
                .findByMembershipId(membershipId)
                .stream()
                .map(MembershipRole::getRole)
                .collect(Collectors.toSet());
    }

    public Set<Permission> getPermissions(Long membershipId) {

        Set<Permission> permissions = new HashSet<>();

        Set<Role> roles = getRoles(membershipId);

        for (Role role : roles) {

            Set<Permission> rolePermissions =
                    rolePermissionRepository
                            .findByRole_RoleId(role.getRoleId())
                            .stream()
                            .map(RolePermission::getPermission)
                            .collect(Collectors.toSet());

            permissions.addAll(rolePermissions);
        }

        return permissions;
    }

    public boolean hasPermission(
            Long membershipId,
            String permissionName) {

        return getPermissions(membershipId)
                .stream()
                .anyMatch(permission ->
                        permission.getName().equals(permissionName));
    }

    public void requirePermission(
            Long membershipId,
            String permissionName) {

        if (!hasPermission(membershipId, permissionName)) {

            throw new AuthorizationException(
                    "You do not have permission to perform this action."
            );
        }
    }

    // ==============================
    // PLATFORM-LEVEL AUTHORIZATION
    // ==============================

    public Set<Role> getUserRoles(Long userId) {

        return userRoleRepository
                .findByUserUserId(userId)
                .stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }

    public Set<Permission> getUserPermissions(Long userId) {

        Set<Permission> permissions = new HashSet<>();

        Set<Role> roles = getUserRoles(userId);

        for (Role role : roles) {

            Set<Permission> rolePermissions =
                    rolePermissionRepository
                            .findByRole_RoleId(role.getRoleId())
                            .stream()
                            .map(RolePermission::getPermission)
                            .collect(Collectors.toSet());

            permissions.addAll(rolePermissions);
        }

        return permissions;
    }

    public boolean hasUserPermission(
            Long userId,
            String permissionName) {

        return getUserPermissions(userId)
                .stream()
                .anyMatch(permission ->
                        permission.getName().equals(permissionName));
    }

    public void requireUserPermission(
            Long userId,
            String permissionName) {

        if (!hasUserPermission(userId, permissionName)) {

            throw new AuthorizationException(
                    "You do not have permission to perform this action."
            );
        }
    }
}