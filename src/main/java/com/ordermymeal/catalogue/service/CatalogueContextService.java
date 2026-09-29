package com.ordermymeal.catalogue.service;

import com.ordermymeal.auth.service.AuthorizationService;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class CatalogueContextService {

    private final AuthorizationService authorizationService;
    private final OrganizationMemberRepository organizationMemberRepository;

    public CatalogueContextService(
            AuthorizationService authorizationService,
            OrganizationMemberRepository organizationMemberRepository) {

        this.authorizationService = authorizationService;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public Long requireOrganization(
            Long membershipId,
            String permission) {

        authorizationService.requirePermission(
                membershipId,
                permission);

        OrganizationMember membership =
                organizationMemberRepository.findById(membershipId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership not found."));

        if (!"ACTIVE".equalsIgnoreCase(membership.getStatus())) {
            throw new IllegalArgumentException(
                    "Membership is not active.");
        }

        if (membership.getOrganization() == null) {
            throw new IllegalArgumentException(
                    "Membership is not linked to an organization.");
        }

        return membership.getOrganization().getOrgId();
    }
}
