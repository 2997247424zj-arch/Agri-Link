package com.example.agrilinkback.system.dto;

import java.time.Instant;

public record HealthStatus(
        String application,
        String status,
        Instant checkedAt
) {
}
