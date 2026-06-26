package com.amazon.seller.devopsportal.service;

import com.amazon.seller.devopsportal.dto.environment.EnvironmentRequest;
import com.amazon.seller.devopsportal.dto.environment.EnvironmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnvironmentManagementService {
    EnvironmentResponse create(EnvironmentRequest request);
    Page<EnvironmentResponse> findAll(Pageable pageable);
    EnvironmentResponse findById(Long id);
    EnvironmentResponse update(Long id, EnvironmentRequest request);
    EnvironmentResponse assignService(Long environmentId, Long serviceId);
    EnvironmentResponse removeService(Long environmentId, Long serviceId);
    void delete(Long id);
}
