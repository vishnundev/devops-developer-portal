package com.amazon.seller.devopsportal.service.impl;

import com.amazon.seller.devopsportal.dto.service.ServiceAction;
import com.amazon.seller.devopsportal.dto.service.ServiceRequest;
import com.amazon.seller.devopsportal.dto.service.ServiceResponse;
import com.amazon.seller.devopsportal.entity.Environment;
import com.amazon.seller.devopsportal.entity.ServiceStatus;
import com.amazon.seller.devopsportal.exception.ResourceNotFoundException;
import com.amazon.seller.devopsportal.repository.ServiceRepository;
import com.amazon.seller.devopsportal.service.ServiceManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@Transactional
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;

    public ServiceManagementServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceResponse create(ServiceRequest request) {
        return toResponse(serviceRepository.save(toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> findAll(Pageable pageable) {
        return serviceRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse findById(Long id) {
        return toResponse(findEntity(id));
    }

    @Override
    public ServiceResponse update(Long id, ServiceRequest request) {
        com.amazon.seller.devopsportal.entity.Service service = findEntity(id);
        service.setName(request.name().trim());
        service.setDescription(normalizeDescription(request.description()));
        service.setModuleName(request.moduleName().trim());
        service.setStatus(request.status());
        service.setVersion(request.version().trim());
        service.setPort(request.port());
        return toResponse(service);
    }

    @Override
    public ServiceResponse changeStatus(Long id, ServiceAction action) {
        com.amazon.seller.devopsportal.entity.Service service = findEntity(id);
        ServiceStatus targetStatus = switch (action) {
            case START -> ServiceStatus.RUNNING;
            case STOP -> ServiceStatus.STOPPED;
            case RESTART -> ServiceStatus.RUNNING;
        };
        service.setStatus(targetStatus);
        return toResponse(service);
    }

    @Override
    public void delete(Long id) {
        serviceRepository.delete(findEntity(id));
    }

    private com.amazon.seller.devopsportal.entity.Service findEntity(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service with ID " + id + " was not found"));
    }

    private com.amazon.seller.devopsportal.entity.Service toEntity(ServiceRequest request) {
        return com.amazon.seller.devopsportal.entity.Service.builder()
                .name(request.name().trim())
                .description(normalizeDescription(request.description()))
                .moduleName(request.moduleName().trim())
                .status(request.status())
                .version(request.version().trim())
                .port(request.port())
                .build();
    }

    private ServiceResponse toResponse(com.amazon.seller.devopsportal.entity.Service service) {
        return new ServiceResponse(service.getId(), service.getName(), service.getDescription(), service.getModuleName(),
                service.getStatus(), service.getVersion(), service.getPort(), service.getCreatedAt(),
                service.getEnvironments().stream().map(Environment::getId).toList());
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank() ? null : description.trim();
    }
}
