package com.ordermymeal.catalogue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ordermymeal.auth.service.CurrentSessionService;
import com.ordermymeal.catalogue.dto.RegisterVendorRequest;
import com.ordermymeal.catalogue.dto.UpdateVendorRequest;
import com.ordermymeal.catalogue.dto.VendorResponse;
import com.ordermymeal.catalogue.service.CatalogueContextService;
import com.ordermymeal.catalogue.service.GetVendorService;
import com.ordermymeal.catalogue.service.GetVendorsService;
import com.ordermymeal.catalogue.service.RegisterVendorService;
import com.ordermymeal.catalogue.service.UpdateVendorService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/catalogue/vendors")
public class VendorController {

    private final RegisterVendorService registerVendorService;
    private final UpdateVendorService updateVendorService;
    private final GetVendorService getVendorService;
    private final GetVendorsService getVendorsService;
    private final CatalogueContextService catalogueContextService;
    private final CurrentSessionService currentSessionService;

    public VendorController(
            RegisterVendorService registerVendorService,
            UpdateVendorService updateVendorService,
            GetVendorService getVendorService,
            GetVendorsService getVendorsService,
            CatalogueContextService catalogueContextService,
            CurrentSessionService currentSessionService) {

        this.registerVendorService = registerVendorService;
        this.updateVendorService = updateVendorService;
        this.getVendorService = getVendorService;
        this.getVendorsService = getVendorsService;
        this.catalogueContextService = catalogueContextService;
        this.currentSessionService = currentSessionService;
    }

    /*
     * ADMIN
     * -----
     * Requires vendor.create permission.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('vendor.create')")
    public ResponseEntity<VendorResponse> registerVendor(
            @Valid @RequestBody RegisterVendorRequest request,
            HttpServletRequest httpRequest) {

        Long membershipId =
                currentSessionService
                        .getCurrentMembershipId(httpRequest);

        VendorResponse response =
                registerVendorService.registerVendor(
                        membershipId,
                        request);

        return ResponseEntity.ok(response);
    }

    /*
     * Requires vendor.view permission.
     */
    @GetMapping("/{vendorId}")
    @PreAuthorize("hasAuthority('vendor.view')")
    public ResponseEntity<VendorResponse> getVendor(
            @PathVariable Long vendorId,
            HttpServletRequest httpRequest) {

        Long membershipId =
                currentSessionService
                        .getCurrentMembershipId(httpRequest);

        Long organizationId =
                catalogueContextService
                        .requireOrganization(
                                membershipId,
                                "vendor.view");

        VendorResponse response =
                getVendorService.execute(
                        vendorId,
                        organizationId);

        return ResponseEntity.ok(response);
    }

    /*
     * Requires vendor.view permission.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('vendor.view')")
    public ResponseEntity<List<VendorResponse>> getVendors(
            HttpServletRequest httpRequest) {

        Long membershipId =
                currentSessionService
                        .getCurrentMembershipId(httpRequest);

        Long organizationId =
                catalogueContextService
                        .requireOrganization(
                                membershipId,
                                "vendor.view");

        List<VendorResponse> response =
                getVendorsService.execute(
                        organizationId);

        return ResponseEntity.ok(response);
    }

    /*
     * Requires vendor.update permission.
     */
    @PutMapping("/{vendorId}")
    @PreAuthorize("hasAuthority('vendor.update')")
    public ResponseEntity<VendorResponse> updateVendor(
            @PathVariable Long vendorId,
            @Valid @RequestBody UpdateVendorRequest request,
            HttpServletRequest httpRequest) {

        Long membershipId =
                currentSessionService
                        .getCurrentMembershipId(httpRequest);

        VendorResponse response =
                updateVendorService.updateVendor(
                        membershipId,
                        vendorId,
                        request);

        return ResponseEntity.ok(response);
    }
}