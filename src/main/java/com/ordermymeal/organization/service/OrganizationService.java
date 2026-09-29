package com.ordermymeal.organization.service;

import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.auth.service.AuthorizationService;
import com.ordermymeal.auth.service.CurrentSessionService;
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
    private final AuthorizationService authorizationService;
    private final CurrentSessionService currentSessionService;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            AuthorizationService authorizationService,
            CurrentSessionService currentSessionService) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
        this.currentSessionService = currentSessionService;
    }

    @Transactional
    public OrganizationResponse createOrganization(
            OrganizationCreateRequest request,
            HttpServletRequest httpRequest) {

        // 1. Validate request
        if (request == null) {
            throw new IllegalArgumentException(
                    "Organization request is required.");
        }

        if (request.getName() == null
                || request.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Organization name is required.");
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

        // 2. Get authenticated SuperAdmin
        Long currentUserId =
                currentSessionService.getCurrentUserId(httpRequest);

        // 3. Check platform-level permission
        //
        // SuperAdmin is identified through user_roles.
        // Organization creation is a platform-level operation.
        authorizationService.requireUserPermission(
                currentUserId,
                "organization.create"
        );

        // 4. Create organization
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

        // 5. Save organization
        Organization savedOrganization =
                organizationRepository.save(organization);

        // 6. Return response
        return toResponse(savedOrganization);
    }

    private OrganizationResponse toResponse(
            Organization organization) {

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

        return response;
    }
}