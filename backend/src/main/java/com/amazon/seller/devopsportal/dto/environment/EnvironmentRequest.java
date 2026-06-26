package com.amazon.seller.devopsportal.dto.environment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnvironmentRequest(
        @NotBlank(message = "Environment name is required")
        @Size(max = 100, message = "Environment name must not exceed 100 characters")
        String name,
        boolean development,
        boolean testing,
        boolean staging,
        boolean production
) {
}
