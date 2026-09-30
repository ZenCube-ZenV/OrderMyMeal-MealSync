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
            String password) {

        String normalizedEmail = email.trim().toLowerCase();

        /*
         * ---------------------------------------------------------
         * 1. Find User
         * ---------------------------------------------------------
         */
        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password."));

        /*
         * ---------------------------------------------------------
         * 2. Validate User
         * ---------------------------------------------------------
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
         * ---------------------------------------------------------
         * 3. Load platform-level roles
         * ---------------------------------------------------------
         */
        List<Role> userRoles =
                userRoleRepository
                        .findByUserUserId(user.getUserId())
                        .stream()
                        .map(UserRole::getRole)
                        .toList();

        /*
         * ---------------------------------------------------------
         * 4. SUPERADMIN LOGIN
         * ---------------------------------------------------------
         *
         * Superadmin is a platform-level user.
         *
         * Therefore:
         *
         * Superadmin
         *     ↓
         * No organization
         * No organization membership required
         */
        boolean superadmin =
                userRoles.stream()
                        .anyMatch(role ->
                                "superadmin".equalsIgnoreCase(
                                        role.getName()));

        if (superadmin) {

            return sessionService.createSuperadminSession(
                    user,
                    userRoles);
        }

        /*
         * ---------------------------------------------------------
         * 5. ORGANIZATION USER LOGIN
         * ---------------------------------------------------------
         *
         * Admin / Vendor / Member
         *
         * These users must have an active organization
         * membership.
         */
        List<OrganizationMember> memberships =
                membershipRepository
                        .findByUserUserIdAndStatusOrderByMembershipIdAsc(
                                user.getUserId(),
                                "ACTIVE");

        /*
         * ---------------------------------------------------------
         * 6. Select organization membership
         * ---------------------------------------------------------
         *
         * organizationId is NOT supplied by the frontend.
         *
         * The organization is determined from the user's
         * active membership.
         */
        OrganizationMember membership =
                selectMembership(memberships);

        /*
         * ---------------------------------------------------------
         * 7. Create organization session
         * ---------------------------------------------------------
         *
         * Session now knows:
         *
         * User
         *    ↓
         * Membership
         *    ↓
         * Organization
         */
        return sessionService.createSession(membership);
    }

    private OrganizationMember selectMembership(
            List<OrganizationMember> memberships) {

        /*
         * No active organization membership.
         */
        if (memberships.isEmpty()) {

            throw new IllegalArgumentException(
                    "No active organization membership found.");
        }

        /*
         * Current application design:
         *
         * One organization-level user
         * belongs to one active organization.
         */
        if (memberships.size() == 1) {

            return memberships.get(0);
        }

        /*
         * If a user eventually belongs to multiple organizations,
         * introduce an organization-selection flow after login.
         */
        throw new IllegalArgumentException(
                "Multiple active organization memberships found. "
                        + "Organization selection is required.");
    }
}