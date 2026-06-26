package com.amazon.seller.devopsportal.dto.service;

import com.amazon.seller.devopsportal.entity.ServiceStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ServiceRequest(
        @NotBlank(message = "Service name is required")
        @Size(max = 100, message = "Service name must not exceed 100 characters")
        String name,
        @Size(max = 65535, message = "Description must not exceed 65535 characters")
        String description,
        @NotBlank(message = "Module name is required")
        @Size(max = 100, message = "Module name must not exceed 100 characters")
        String moduleName,
        @NotNull(message = "Service status is required")
        ServiceStatus status,
        @NotBlank(message = "Version is required")
        @Size(max = 50, message = "Version must not exceed 50 characters")
        String version,
        @NotNull(message = "Port is required")
        @Max(value = 65535, message = "Port must not exceed 65535")
        @jakarta.validation.constraints.Min(value = 1, message = "Port must be at least 1")
        Integer port
) {
}
