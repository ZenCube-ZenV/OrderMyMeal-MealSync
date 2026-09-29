package com.ordermymeal.auth.service;

import com.ordermymeal.auth.model.Role;
import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.model.UserRole;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.auth.repository.UserRoleRepository;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PasswordLoginService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final OrganizationMemberRepository membershipRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final AuthenticationSessionService sessionService;

    public PasswordLoginService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            OrganizationMemberRepository membershipRepository,
            PasswordEncoderService passwordEncoderService,
            AuthenticationSessionService sessionService) {

        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.membershipRepository = membershipRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.sessionService = sessionService;
    }

    @Transactional
    public AuthenticationSessionService.SessionResult login(
            String email,
            String password,
            Long organizationId) {

        String normalizedEmail = email.trim().toLowerCase();

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid email or password."));

        /*
         * User must be active and have a password.
         */
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())
                || user.getPasswordHash() == null
                || !passwordEncoderService.matches(
                        password,
                        user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "Invalid email or password.");
        }

        /*
         * Load platform-level roles assigned directly
         * to the user.
         */
        List<Role> userRoles = userRoleRepository
                .findByUserUserId(user.getUserId())
                .stream()
                .map(UserRole::getRole)
                .toList();

        /*
         * Super Admin is a platform-level role.
         *
         * Super Admin does NOT require:
         * - organizationId
         * - organization_members record
         *
         * Super Admin logs in using email + password.
         */
        boolean superadmin = userRoles.stream()
                .anyMatch(role -> "superadmin".equalsIgnoreCase(role.getName()));

        if (superadmin) {
            return sessionService.createSuperadminSession(
                    user,
                    userRoles);
        }

        /*
         * Admin, User and Vendor are organization-level users.
         *
         * They must have an ACTIVE organization membership.
         */
        List<OrganizationMember> memberships = membershipRepository
                .findByUserUserIdAndStatusOrderByMembershipIdAsc(
                        user.getUserId(),
                        "ACTIVE");

        OrganizationMember membership = selectMembership(
                memberships,
                organizationId);

        return sessionService.createSession(
                membership);
    }

    private OrganizationMember selectMembership(
            List<OrganizationMember> memberships,
            Long organizationId) {

        /*
         * No active organization membership.
         */
        if (memberships.isEmpty()) {
            throw new IllegalArgumentException(
                    "No active organization membership found.");
        }

        /*
         * User belongs to a specific organization.
         */
        if (organizationId != null) {

            return memberships.stream()
                    .filter(m -> m.getOrganization() != null
                            && organizationId.equals(
                                    m.getOrganization().getOrgId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No active membership found for the selected organization."));
        }

        /*
         * User has multiple organizations.
         * The login request must specify which organization
         * they want to enter.
         */
        if (memberships.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple active memberships found. organizationId is required.");
        }

        /*
         * User belongs to exactly one organization.
         */
        return memberships.get(0);
    }
}

