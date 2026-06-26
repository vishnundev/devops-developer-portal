package com.amazon.seller.devopsportal.dto.environment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ServiceAssignmentRequest(
        @NotNull(message = "Service ID is required")
        @Positive(message = "Service ID must be positive")
        Long serviceId,

        @NotNull(message = "Environment ID is required")
        @Positive(message = "Environment ID must be positive")
        Long environmentId,

        @NotBlank(message = "Version is required")
        String version
) {
}