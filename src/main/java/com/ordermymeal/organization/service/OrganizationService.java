package com.ordermymeal.organization.service;

import com.ordermymeal.auth.model.Role;
import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.repository.RoleRepository;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.auth.service.CurrentSessionService;
import com.ordermymeal.membership.model.MembershipRole;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.MembershipRoleRepository;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import com.ordermymeal.organization.dto.OrganizationCreateRequest;
import com.ordermymeal.organization.dto.OrganizationResponse;
import com.ordermymeal.organization.model.Organization;
import com.ordermymeal.organization.repository.OrganizationRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final MembershipRoleRepository membershipRoleRepository;
    private final CurrentSessionService currentSessionService;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            OrganizationMemberRepository organizationMemberRepository,
            MembershipRoleRepository membershipRoleRepository,
            CurrentSessionService currentSessionService) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.membershipRoleRepository = membershipRoleRepository;
        this.currentSessionService = currentSessionService;
    }

    @Transactional
    public OrganizationResponse createOrganization(
            OrganizationCreateRequest request,
            HttpServletRequest httpRequest) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Organization request is required.");
        }

        if (request.getName() == null
                || request.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Organization name is required.");
        }

        if (request.getAdminEmail() == null
                || request.getAdminEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Admin email is required.");
        }

        String organizationName =
                request.getName().trim();

        String displayName =
                request.getDisplayName() == null
                        || request.getDisplayName().isBlank()
                        ? organizationName
                        : request.getDisplayName().trim();

        String timeZone =
                request.getTimeZone() == null
                        || request.getTimeZone().isBlank()
                        ? "Asia/Kolkata"
                        : request.getTimeZone().trim();

        String adminEmail =
                request.getAdminEmail()
                        .trim()
                        .toLowerCase();

        String adminName =
                request.getAdminName() == null
                        || request.getAdminName().isBlank()
                        ? null
                        : request.getAdminName().trim();

        /*
         * The controller already protects this operation with:
         *
         * @PreAuthorize("hasAuthority('organization.create')")
         *
         * Resolve the current session so the request is tied to
         * an authenticated user.
         */
        currentSessionService.getCurrentUserId(httpRequest);

        /*
         * ---------------------------------------------------------
         * 1. Find or create admin user
         * ---------------------------------------------------------
         */
        User adminUser =
                userRepository.findByEmail(adminEmail)
                        .orElseGet(() -> {

                            User newUser = new User();

                            newUser.setEmail(adminEmail);
                            newUser.setName(adminName);
                            newUser.setStatus("ACTIVE");
                            newUser.setCreatedAt(Instant.now());

                            /*
                             * No password is created here.
                             * Admin will set the password through
                             * the existing password setup flow.
                             */
                            newUser.setPasswordHash(null);

                            return userRepository.save(newUser);
                        });

        /*
         * Existing admin user must be active.
         */
        if (!"ACTIVE".equalsIgnoreCase(
                adminUser.getStatus())) {

            throw new IllegalArgumentException(
                    "Admin user account is not active.");
        }

        /*
         * Update name only when the existing user does not
         * already have one.
         */
        if (adminName != null
                && (adminUser.getName() == null
                || adminUser.getName().isBlank())) {

            adminUser.setName(adminName);
            adminUser = userRepository.save(adminUser);
        }

        /*
         * ---------------------------------------------------------
         * 2. Create organization
         * ---------------------------------------------------------
         */
        Organization organization =
                new Organization();

        organization.setName(organizationName);
        organization.setDisplayName(displayName);
        organization.setTimeZone(timeZone);
        organization.setStatus("ACTIVE");
        organization.setNextOrderNumber(1L);
        organization.setRazorpayConfigVersion(1);
        organization.setRepublishAfterCancelAllowed(false);
        organization.setCreatedAt(Instant.now());

        Organization savedOrganization =
                organizationRepository.save(organization);

        /*
         * ---------------------------------------------------------
         * 3. Create organization membership
         * ---------------------------------------------------------
         */
        OrganizationMember membership =
                new OrganizationMember();

        membership.setUser(adminUser);
        membership.setOrganization(savedOrganization);
        membership.setStatus("ACTIVE");
        membership.setCreatedAt(Instant.now());

        OrganizationMember savedMembership =
                organizationMemberRepository.save(membership);

        /*
         * ---------------------------------------------------------
         * 4. Find ADMIN role
         * ---------------------------------------------------------
         */
        Role adminRole =
                roleRepository.findByNameIgnoreCase("admin")
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "ADMIN role is not configured."));

        /*
         * ---------------------------------------------------------
         * 5. Assign ADMIN role to membership
         * ---------------------------------------------------------
         */
        MembershipRole membershipRole =
                new MembershipRole();

        membershipRole.setMembershipId(
                savedMembership.getMembershipId());

        membershipRole.setRole(adminRole);
        membershipRole.setCreatedAt(Instant.now());
        membershipRole.setUpdatedAt(Instant.now());

        membershipRoleRepository.save(membershipRole);

        /*
         * ---------------------------------------------------------
         * 6. Return response
         * ---------------------------------------------------------
         */
        return toResponse(
                savedOrganization,
                adminUser);
    }

    private OrganizationResponse toResponse(
            Organization organization,
            User adminUser) {

        OrganizationResponse response =
                new OrganizationResponse();

        response.setOrgId(
                organization.getOrgId());

        response.setName(
                organization.getName());

        response.setDisplayName(
                organization.getDisplayName());

        response.setTimeZone(
                organization.getTimeZone());

        response.setStatus(
                organization.getStatus());

        response.setNextOrderNumber(
                organization.getNextOrderNumber());

        response.setCreatedAt(
                organization.getCreatedAt());

        response.setAdminEmail(
                adminUser.getEmail());

        response.setAdminName(
                adminUser.getName());

        return response;
    }
}