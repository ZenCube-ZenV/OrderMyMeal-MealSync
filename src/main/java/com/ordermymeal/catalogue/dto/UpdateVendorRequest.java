package com.ordermymeal.catalogue.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateVendorRequest(

        @NotBlank(message = "Vendor name is required")
        String name,

        @NotBlank(message = "Contact person is required")
        String contactPerson,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Enter a valid contact email")
        String contactEmail,

        @NotBlank(message = "Contact phone is required")
        String contactPhone,

        String taxIdentifier,

        @NotNull(message = "Lead minutes are required")
        @PositiveOrZero(message = "Lead minutes cannot be negative")
        Integer leadMinutes,

        @NotBlank(message = "Status is required")
        String status
) {
} 

