package com.ordermymeal.membership.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(

        @NotBlank(message = "Name is required")
        String name
) {
}  