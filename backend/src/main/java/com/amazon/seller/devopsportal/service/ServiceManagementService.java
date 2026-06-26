package com.amazon.seller.devopsportal.service;

import com.amazon.seller.devopsportal.dto.service.ServiceAction;
import com.amazon.seller.devopsportal.dto.service.ServiceRequest;
import com.amazon.seller.devopsportal.dto.service.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceManagementService {
    ServiceResponse create(ServiceRequest request);
    Page<ServiceResponse> findAll(Pageable pageable);
    ServiceResponse findById(Long id);
    ServiceResponse update(Long id, ServiceRequest request);
    ServiceResponse changeStatus(Long id, ServiceAction action);
    void delete(Long id);
}
