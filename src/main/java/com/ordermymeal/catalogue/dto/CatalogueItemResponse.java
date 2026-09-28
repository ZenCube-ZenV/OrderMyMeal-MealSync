package com.ordermymeal.catalogue.dto;

public record CatalogueItemResponse(

        Long catalogueItemId,

        Long organizationId,

        Long vendorId,

        String name,

        String description,

        String category,

        String dietaryTag,

        String photoObjectKey,

        Long priceMinor,

        String currency,

        Integer gstRateBp,

        String status
) {
}    