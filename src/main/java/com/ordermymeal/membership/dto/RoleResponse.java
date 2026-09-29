package com.ordermymeal.membership.dto;

import java.util.UUID;

public record RoleResponse(
        UUID roleId,
        String name,
        String description
) {
} 