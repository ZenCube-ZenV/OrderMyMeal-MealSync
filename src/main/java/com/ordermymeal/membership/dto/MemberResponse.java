package com.ordermymeal.membership.dto;

import java.time.Instant;
import java.util.List;

public record MemberResponse(
        Long membershipId,
        Long userId,
        Long organizationId,
        String email,
        String name,
        String status,
        List<String> roles,
        Instant createdAt
) {
} 