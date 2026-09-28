package com.ordermymeal.catalogue.service;

import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.catalogue.dto.RegisterVendorRequest;
import com.ordermymeal.catalogue.dto.VendorResponse;
import com.ordermymeal.catalogue.model.Vendor;
import com.ordermymeal.catalogue.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RegisterVendorService {

    private final VendorRepository vendorRepository;
    private final CatalogueContextService catalogueContextService;

    public RegisterVendorService(
            VendorRepository vendorRepository,
            CatalogueContextService catalogueContextService) {

        this.vendorRepository = vendorRepository;
        this.catalogueContextService = catalogueContextService;
    }

    @Transactional
    public VendorResponse registerVendor(
            Long membershipId,
            RegisterVendorRequest request) {

        Long organizationId =
                catalogueContextService.requireOrganization(
                        membershipId,
                        Permission.VENDOR_MANAGE);

        Instant now = Instant.now();

        Vendor vendor = new Vendor();

        vendor.setOrganizationId(organizationId);
        vendor.setName(request.name().trim());
        vendor.setContactPerson(request.contactPerson().trim());
        vendor.setContactEmail(
                request.contactEmail().trim().toLowerCase());
        vendor.setContactPhone(request.contactPhone().trim());
        vendor.setTaxIdentifier(request.taxIdentifier());
        vendor.setLeadMinutes(request.leadMinutes());
        vendor.setStatus("ACTIVE");
        vendor.setCreatedAt(now);
        vendor.setUpdatedAt(now);

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