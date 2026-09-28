package com.ordermymeal.catalogue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCatalogueItemRequest(

        @NotBlank(message = "Item name is required")
        String name,

        String description,

        String category,

        String dietaryTag,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        Long priceMinor,

        String currency,

        @NotNull(message = "GST rate is required")
        @PositiveOrZero(message = "GST rate cannot be negative")
        Integer gstRateBp,

        @NotBlank(message = "Status is required")
        String status
) {
}   