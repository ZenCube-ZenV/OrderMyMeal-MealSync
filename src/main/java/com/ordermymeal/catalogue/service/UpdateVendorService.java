package com.ordermymeal.catalogue.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.catalogue.dto.UpdateVendorRequest;
import com.ordermymeal.catalogue.dto.VendorResponse;
import com.ordermymeal.catalogue.model.Vendor;
import com.ordermymeal.catalogue.repository.VendorRepository;

@Service
public class UpdateVendorService {

    private final VendorRepository vendorRepository;
    private final CatalogueContextService catalogueContextService;

    public UpdateVendorService(
            VendorRepository vendorRepository,
            CatalogueContextService catalogueContextService) {

        this.vendorRepository = vendorRepository;
        this.catalogueContextService = catalogueContextService;
    }

    @Transactional
    public VendorResponse updateVendor(
            Long membershipId,
            Long vendorId,
            UpdateVendorRequest request) {

        Long organizationId =
                catalogueContextService.requireOrganization(
                        membershipId,
                        Permission.VENDOR_MANAGE);

        Vendor vendor =
                vendorRepository
                        .findByVendorIdAndOrganizationId(
                                vendorId,
                                organizationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Vendor not found."));

        vendor.setName(request.name().trim());
        vendor.setContactPerson(
                request.contactPerson().trim());
        vendor.setContactEmail(
                request.contactEmail().trim().toLowerCase());
        vendor.setContactPhone(
                request.contactPhone().trim());
        vendor.setTaxIdentifier(
                request.taxIdentifier());
        vendor.setLeadMinutes(
                request.leadMinutes());

        String status = request.status().trim().toUpperCase();

        if (!status.equals("ACTIVE")
                && !status.equals("INACTIVE")) {

            throw new IllegalArgumentException(
                    "Vendor status must be ACTIVE or INACTIVE.");
        }

        vendor.setStatus(status);

        if ("INACTIVE".equals(status)) {
            if (vendor.getDeactivatedAt() == null) {
                vendor.setDeactivatedAt(Instant.now());
            }
        } else {
            vendor.setDeactivatedAt(null);
        }

        vendor.setUpdatedAt(Instant.now());

        Vendor savedVendor = vendorRepository.save(vendor);

        return toResponse(savedVendor);
    }

    private VendorResponse toResponse(Vendor vendor) {

        return new VendorResponse(
                vendor.getVendorId(),
                vendor.getOrganizationId(),
                vendor.getName(),
                vendor.getContactPerson(),
                vendor.getContactEmail(),
                vendor.getContactPhone(),
                vendor.getTaxIdentifier(),
                vendor.getLeadMinutes(),
                vendor.getStatus());
    }
}  