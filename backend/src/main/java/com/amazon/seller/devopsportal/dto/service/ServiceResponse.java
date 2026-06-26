package com.amazon.seller.devopsportal.dto.service;

import com.amazon.seller.devopsportal.entity.ServiceStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ServiceResponse(Long id, String name, String description, String moduleName, ServiceStatus status,
                              String version, Integer port, LocalDateTime createdAt, List<Long> environmentIds) {
}
