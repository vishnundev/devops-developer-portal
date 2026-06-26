package com.amazon.seller.devopsportal.service;

import com.amazon.seller.devopsportal.dto.deployment.DeploymentResponse;
import com.amazon.seller.devopsportal.dto.environment.ServiceAssignmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeploymentManagementService {
    Page<DeploymentResponse> findHistory(Long environmentId, Pageable pageable);
    // ADD THIS LINE
    DeploymentResponse deployService(ServiceAssignmentRequest request);
}