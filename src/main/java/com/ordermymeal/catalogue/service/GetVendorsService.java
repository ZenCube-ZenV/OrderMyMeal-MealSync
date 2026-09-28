package com.ordermymeal.catalogue.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ordermymeal.catalogue.dto.VendorResponse;
import com.ordermymeal.catalogue.model.Vendor;
import com.ordermymeal.catalogue.repository.VendorRepository;

@Service
public class GetVendorsService {

    private final VendorRepository vendorRepository;

    public GetVendorsService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public List<VendorResponse> execute(Long organizationId) {

        return vendorRepository
                .findByOrganizationIdOrderByVendorIdDesc(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
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
                vendor.getStatus()
        );
    }
}  