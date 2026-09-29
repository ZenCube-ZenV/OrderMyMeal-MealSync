package com.ordermymeal.membership.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleAssignmentRequest(

        @NotBlank(message = "Role name is required")
        String role
) {
} 