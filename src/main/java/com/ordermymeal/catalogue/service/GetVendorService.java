package com.ordermymeal.catalogue.service;

import com.ordermymeal.catalogue.dto.VendorResponse;
import com.ordermymeal.catalogue.model.Vendor;
import com.ordermymeal.catalogue.repository.VendorRepository;
import org.springframework.stereotype.Service;

@Service
public class GetVendorService {

    private final VendorRepository vendorRepository;

    public GetVendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public VendorResponse execute(
            Long vendorId,
            Long organizationId) {

        Vendor vendor = vendorRepository
                .findByVendorIdAndOrganizationId(
                        vendorId,
                        organizationId
                )
                .orElseThrow(() ->
                        new CatalogueNotFoundException(
                                "Vendor not found"
                        )
                );

        return new VendorResponse(
                vendor.getVendorId(),
                vendor.getOrganizationId(),
                vendor.getName(),
                vendor.getContactPerson(),
                vendor.getContactEmail(),
                vendor.getContactPhone(),
                vendor.getTaxIdentifier(),
                vendor.getLeadMinutes(),
                vendor.getStatus()
        );
    }
}  

