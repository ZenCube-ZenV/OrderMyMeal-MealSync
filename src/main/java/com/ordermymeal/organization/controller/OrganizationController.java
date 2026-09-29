package com.ordermymeal.organization.controller;

import com.ordermymeal.organization.dto.OrganizationCreateRequest;
import com.ordermymeal.organization.dto.OrganizationResponse;
import com.ordermymeal.organization.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(
            OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('organization.create')")
    public ResponseEntity<OrganizationResponse> createOrganization(
            @RequestBody OrganizationCreateRequest request,
            HttpServletRequest httpRequest) {

        OrganizationResponse response = organizationService.createOrganization(
                request,
                httpRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
