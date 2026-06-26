package com.amazon.seller.devopsportal.dto.deployment;

import jakarta.validation.constraints.Positive;

public record DeploymentHistoryFilter(
        @Positive(message = "Environment ID must be positive")
        Long environmentId
) {
}
