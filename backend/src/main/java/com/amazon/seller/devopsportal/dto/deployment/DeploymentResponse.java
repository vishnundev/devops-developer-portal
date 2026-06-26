package com.amazon.seller.devopsportal.dto.deployment;

import com.amazon.seller.devopsportal.entity.DeploymentStatus;

import java.time.LocalDateTime;

public record DeploymentResponse(Long id, Long serviceId, String serviceName, Long environmentId,
                                 String environmentName, String version, DeploymentStatus deploymentStatus,
                                 LocalDateTime deploymentTime) {
}
