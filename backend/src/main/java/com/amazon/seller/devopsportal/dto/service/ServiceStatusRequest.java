package com.amazon.seller.devopsportal.dto.service;

import jakarta.validation.constraints.NotNull;

public record ServiceStatusRequest(@NotNull(message = "Action is required") ServiceAction action) {
}
