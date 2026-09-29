package com.ordermymeal.membership.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record MemberCreateRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        String email,

        @NotBlank(message = "Name is required")
        String name,

        @NotEmpty(message = "At least one role is required")
        List<@NotBlank(message = "Role name cannot be blank") String> roles
) {
}   