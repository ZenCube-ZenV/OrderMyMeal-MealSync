package com.ordermymeal.membership.service;

import com.ordermymeal.auth.model.Role;
import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.repository.RoleRepository;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.auth.service.AuthorizationService;
import com.ordermymeal.auth.service.CurrentSessionService;
import com.ordermymeal.membership.dto.MemberCreateRequest;
import com.ordermymeal.membership.dto.MemberResponse;
import com.ordermymeal.membership.dto.MemberUpdateRequest;
import com.ordermymeal.membership.dto.RoleResponse;
import com.ordermymeal.membership.model.MembershipRole;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.MembershipRoleRepository;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class MembershipService {

    private static final String ACTIVE = "ACTIVE";
    private static final String DEACTIVATED = "DEACTIVATED";

    private final OrganizationMemberRepository membershipRepository;
    private final MembershipRoleRepository membershipRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorizationService authorizationService;
    private final CurrentSessionService currentSessionService;

    public MembershipService(
            OrganizationMemberRepository membershipRepository,
            MembershipRoleRepository membershipRoleRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthorizationService authorizationService,
            CurrentSessionService currentSessionService) {

        this.membershipRepository = membershipRepository;
        this.membershipRoleRepository = membershipRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authorizationService = authorizationService;
        this.currentSessionService = currentSessionService;
    }

    @Transactional
    public MemberResponse createMember(
            MemberCreateRequest request,
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "member.create");

        Long organizationId =
                getOrganizationId(currentMembershipId);

        String email =
                normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
                .orElseGet(() ->
                        createUser(email, request.name()));

        if (membershipRepository
                .findByUserUserIdAndOrganizationOrgId(
                        user.getUserId(),
                        organizationId)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "This user is already a member of the organization.");
        }

        /*
         * Creating membership roles requires role.assign.
         */
        authorizationService.requirePermission(
                currentMembershipId,
                "role.assign");

        List<Role> roles =
                resolveRoles(request.roles());

        OrganizationMember currentMembership =
                membershipRepository.findById(currentMembershipId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Current membership not found."));

        OrganizationMember membership =
                new OrganizationMember();

        membership.setUser(user);
        membership.setOrganization(
                currentMembership.getOrganization());
        membership.setStatus(ACTIVE);
        membership.setCreatedAt(Instant.now());

        OrganizationMember savedMembership =
                membershipRepository.save(membership);

        saveRoles(
                savedMembership.getMembershipId(),
                roles);

        return toResponse(savedMembership);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers(
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "member.view");

        Long organizationId =
                getOrganizationId(currentMembershipId);

        return membershipRepository
                .findByOrganizationOrgIdOrderByMembershipIdAsc(
                        organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(
            Long membershipId,
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "member.view");

        OrganizationMember membership =
                getMembershipInCurrentOrganization(
                        membershipId,
                        currentMembershipId);

        return toResponse(membership);
    }

    @Transactional
    public MemberResponse updateMember(
            Long membershipId,
            MemberUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "member.update");

        OrganizationMember membership =
                getMembershipInCurrentOrganization(
                        membershipId,
                        currentMembershipId);

        if (!ACTIVE.equalsIgnoreCase(
                membership.getStatus())) {

            throw new IllegalArgumentException(
                    "Only an active membership can be updated.");
        }

        User user =
                membership.getUser();

        user.setName(
                request.name().trim());

        userRepository.save(user);

        return toResponse(membership);
    }

    @Transactional
    public MemberResponse deactivateMember(
            Long membershipId,
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "member.deactivate");

        if (membershipId.equals(currentMembershipId)) {

            throw new IllegalArgumentException(
                    "You cannot deactivate your own active membership.");
        }

        OrganizationMember membership =
                getMembershipInCurrentOrganization(
                        membershipId,
                        currentMembershipId);

        if (!ACTIVE.equalsIgnoreCase(
                membership.getStatus())) {

            throw new IllegalArgumentException(
                    "Membership is already inactive.");
        }

        membership.setStatus(DEACTIVATED);

        return toResponse(
                membershipRepository.save(membership));
    }

    @Transactional
    public MemberResponse assignRole(
            Long membershipId,
            String roleName,
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "role.assign");

        OrganizationMember membership =
                getMembershipInCurrentOrganization(
                        membershipId,
                        currentMembershipId);

        if (!ACTIVE.equalsIgnoreCase(
                membership.getStatus())) {

            throw new IllegalArgumentException(
                    "Cannot assign a role to an inactive membership.");
        }

        Role role =
                findRole(roleName);

        if (membershipRoleRepository
                .existsByMembershipIdAndRole_RoleId(
                        membershipId,
                        role.getRoleId())) {

            throw new IllegalArgumentException(
                    "Role is already assigned to this member.");
        }

        MembershipRole membershipRole =
                new MembershipRole();

        Instant now =
                Instant.now();

        membershipRole.setMembershipId(
                membershipId);

        membershipRole.setRole(role);

        membershipRole.setCreatedAt(now);

        membershipRole.setUpdatedAt(now);

        membershipRoleRepository.save(
                membershipRole);

        return toResponse(membership);
    }

    @Transactional
    public MemberResponse removeRole(
            Long membershipId,
            String roleName,
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "role.assign");

        OrganizationMember membership =
                getMembershipInCurrentOrganization(
                        membershipId,
                        currentMembershipId);

        Role role =
                findRole(roleName);

        MembershipRole membershipRole =
                membershipRoleRepository
                        .findByMembershipIdAndRole_RoleId(
                                membershipId,
                                role.getRoleId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Role is not assigned to this member."));

        List<MembershipRole> assignedRoles =
                membershipRoleRepository
                        .findByMembershipId(membershipId);

        if (assignedRoles.size() <= 1) {

            throw new IllegalArgumentException(
                    "A member must have at least one role.");
        }

        membershipRoleRepository.delete(
                membershipRole);

        return toResponse(membership);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAvailableRoles(
            HttpServletRequest httpRequest) {

        Long currentMembershipId =
                currentSessionService.getCurrentMembershipId(httpRequest);

        authorizationService.requirePermission(
                currentMembershipId,
                "role.view");

        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleResponse(
                        role.getRoleId(),
                        role.getName(),
                        role.getDescription()))
                .toList();
    }

    private User createUser(
            String email,
            String name) {

        if (name == null || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Name is required.");
        }

        User user =
                new User();

        user.setEmail(email);

        user.setName(
                name.trim());

        user.setStatus(ACTIVE);

        user.setCreatedAt(
                Instant.now());

        return userRepository.save(user);
    }

    /**
     * Normalizes and validates an email address.
     */
    private String normalizeEmail(
            String email) {

        if (email == null || email.isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required.");
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private List<Role> resolveRoles(
            List<String> requestedRoles) {

        if (requestedRoles == null) {

            throw new IllegalArgumentException(
                    "At least one valid role is required.");
        }

        Set<String> normalizedNames =
                new LinkedHashSet<>();

        for (String requestedRole :
                requestedRoles) {

            if (requestedRole == null
                    || requestedRole.isBlank()) {

                continue;
            }

            normalizedNames.add(
                    requestedRole
                            .trim()
                            .toLowerCase(Locale.ROOT));
        }

        if (normalizedNames.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one valid role is required.");
        }

        List<Role> roles =
                new ArrayList<>();

        for (String roleName :
                normalizedNames) {

            roles.add(
                    findRole(roleName));
        }

        return roles;
    }

    private Role findRole(
            String roleName) {

        String normalizedRole =
                roleName == null
                        ? ""
                        : roleName
                                .trim()
                                .toLowerCase(Locale.ROOT);

        if (normalizedRole.isBlank()) {

            throw new IllegalArgumentException(
                    "Role name is required.");
        }

        /*
         * Superadmin is a platform-level role.
         * It must not be assigned through membership_roles.
         */
        if ("superadmin".equals(normalizedRole)) {

            throw new IllegalArgumentException(
                    "superadmin is a platform role and cannot be assigned to an organization membership.");
        }

        return roleRepository
                .findByNameIgnoreCase(
                        normalizedRole)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role not found: "
                                        + normalizedRole));
    }

    private void saveRoles(
            Long membershipId,
            List<Role> roles) {

        Instant now =
                Instant.now();

        for (Role role :
                roles) {

            MembershipRole membershipRole =
                    new MembershipRole();

            membershipRole.setMembershipId(
                    membershipId);

            membershipRole.setRole(role);

            membershipRole.setCreatedAt(now);

            membershipRole.setUpdatedAt(now);

            membershipRoleRepository.save(
                    membershipRole);
        }
    }

    private Long getOrganizationId(
            Long currentMembershipId) {

        OrganizationMember currentMembership =
                membershipRepository
                        .findById(currentMembershipId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Current membership not found."));

        if (!ACTIVE.equalsIgnoreCase(
                currentMembership.getStatus())) {

            throw new IllegalArgumentException(
                    "Current membership is not active.");
        }

        if (currentMembership.getOrganization() == null) {

            throw new IllegalArgumentException(
                    "Current membership is not linked to an organization.");
        }

        return currentMembership
                .getOrganization()
                .getOrgId();
    }

    private OrganizationMember
    getMembershipInCurrentOrganization(
            Long targetMembershipId,
            Long currentMembershipId) {

        if (targetMembershipId == null) {

            throw new IllegalArgumentException(
                    "Membership id is required.");
        }

        Long currentOrganizationId =
                getOrganizationId(
                        currentMembershipId);

        OrganizationMember membership =
                membershipRepository
                        .findById(targetMembershipId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership not found."));

        if (membership.getOrganization() == null
                || !currentOrganizationId.equals(
                        membership
                                .getOrganization()
                                .getOrgId())) {

            throw new IllegalArgumentException(
                    "Membership does not belong to your organization.");
        }

        return membership;
    }

    private MemberResponse toResponse(
            OrganizationMember membership) {

        Long organizationId =
                membership.getOrganization() == null
                        ? null
                        : membership
                                .getOrganization()
                                .getOrgId();

        User user =
                membership.getUser();

        List<String> roles =
                membershipRoleRepository
                        .findByMembershipId(
                                membership.getMembershipId())
                        .stream()
                        .map(MembershipRole::getRole)
                        .map(Role::getName)
                        .sorted()
                        .toList();

        return new MemberResponse(
                membership.getMembershipId(),
                user.getUserId(),
                organizationId,
                user.getEmail(),
                user.getName(),
                membership.getStatus(),
                roles,
                membership.getCreatedAt());
    }  
}