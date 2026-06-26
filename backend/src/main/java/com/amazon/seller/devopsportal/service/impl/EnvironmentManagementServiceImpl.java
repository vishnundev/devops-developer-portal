package com.amazon.seller.devopsportal.service.impl;

import com.amazon.seller.devopsportal.dto.environment.EnvironmentRequest;
import com.amazon.seller.devopsportal.dto.environment.EnvironmentResponse;
import com.amazon.seller.devopsportal.entity.Environment;
import com.amazon.seller.devopsportal.exception.ResourceAlreadyExistsException;
import com.amazon.seller.devopsportal.exception.ResourceNotFoundException;
import com.amazon.seller.devopsportal.repository.EnvironmentRepository;
import com.amazon.seller.devopsportal.repository.ServiceRepository;
import com.amazon.seller.devopsportal.service.EnvironmentManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@Transactional
public class EnvironmentManagementServiceImpl implements EnvironmentManagementService {

    private final EnvironmentRepository environmentRepository;
    private final ServiceRepository serviceRepository;

    public EnvironmentManagementServiceImpl(EnvironmentRepository environmentRepository, ServiceRepository serviceRepository) {
        this.environmentRepository = environmentRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public EnvironmentResponse create(EnvironmentRequest request) {
        String name = request.name().trim();
        if (environmentRepository.findByName(name).isPresent()) {
            throw new ResourceAlreadyExistsException("An environment with this name already exists");
        }
        validateSingleType(request);
        Environment environment = Environment.builder()
                .name(name)
                .development(request.development())
                .testing(request.testing())
                .staging(request.staging())
                .production(request.production())
                .build();
        return toResponse(environmentRepository.save(environment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnvironmentResponse> findAll(Pageable pageable) {
        return environmentRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvironmentResponse findById(Long id) {
        return toResponse(findEntity(id));
    }

    @Override
    public EnvironmentResponse update(Long id, EnvironmentRequest request) {
        Environment environment = findEntity(id);
        String name = request.name().trim();
        environmentRepository.findByName(name)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new ResourceAlreadyExistsException("An environment with this name already exists"); });
        validateSingleType(request);
        environment.setName(name);
        environment.setDevelopment(request.development());
        environment.setTesting(request.testing());
        environment.setStaging(request.staging());
        environment.setProduction(request.production());
        return toResponse(environment);
    }

    @Override
    public EnvironmentResponse assignService(Long environmentId, Long serviceId) {
        Environment environment = findEntity(environmentId);
        com.amazon.seller.devopsportal.entity.Service service = findService(serviceId);
        if (service.getEnvironments().stream().noneMatch(item -> item.getId().equals(environmentId))) {
            service.getEnvironments().add(environment);
            environment.getServices().add(service);
        }
        return toResponse(environment);
    }

    @Override
    public EnvironmentResponse removeService(Long environmentId, Long serviceId) {
        Environment environment = findEntity(environmentId);
        com.amazon.seller.devopsportal.entity.Service service = findService(serviceId);
        boolean removed = service.getEnvironments().removeIf(item -> item.getId().equals(environmentId));
        if (!removed) {
            throw new ResourceNotFoundException("Service with ID " + serviceId + " is not assigned to this environment");
        }
        environment.getServices().removeIf(item -> item.getId().equals(serviceId));
        return toResponse(environment);
    }

    @Override
    public void delete(Long id) {
        environmentRepository.delete(findEntity(id));
    }

    private Environment findEntity(Long id) {
        return environmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Environment with ID " + id + " was not found"));
    }

    private com.amazon.seller.devopsportal.entity.Service findService(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service with ID " + id + " was not found"));
    }

    private EnvironmentResponse toResponse(Environment environment) {
        return new EnvironmentResponse(environment.getId(), environment.getName(), environment.isDevelopment(),
                environment.isTesting(), environment.isStaging(), environment.isProduction(),
                environment.getServices().stream().map(com.amazon.seller.devopsportal.entity.Service::getId).toList());
    }

    private void validateSingleType(EnvironmentRequest request) {
        int selectedTypes = (request.development() ? 1 : 0) + (request.testing() ? 1 : 0)
                + (request.staging() ? 1 : 0) + (request.production() ? 1 : 0);
        if (selectedTypes != 1) {
            throw new IllegalArgumentException("Exactly one environment type must be selected");
        }
    }
}
