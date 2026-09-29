package com.ordermymeal.catalogue.dto;

public record VendorResponse(
        Long vendorId,
        Long organizationId,
        String name,
        String contactPerson,
        String contactEmail,
        String contactPhone,
        String taxIdentifier,
        Integer leadMinutes,
        String status
) {
}


