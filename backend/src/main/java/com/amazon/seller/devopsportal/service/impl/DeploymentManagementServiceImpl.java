package com.amazon.seller.devopsportal.service.impl;

import com.amazon.seller.devopsportal.dto.deployment.DeploymentResponse;
import com.amazon.seller.devopsportal.dto.environment.ServiceAssignmentRequest;
import com.amazon.seller.devopsportal.entity.Deployment;
import com.amazon.seller.devopsportal.entity.DeploymentStatus;
import com.amazon.seller.devopsportal.entity.Environment;
import com.amazon.seller.devopsportal.entity.Service;
import com.amazon.seller.devopsportal.exception.ResourceNotFoundException;
import com.amazon.seller.devopsportal.repository.DeploymentRepository;
import com.amazon.seller.devopsportal.repository.EnvironmentRepository;
import com.amazon.seller.devopsportal.repository.ServiceRepository;
import com.amazon.seller.devopsportal.service.DeploymentManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
public class DeploymentManagementServiceImpl implements DeploymentManagementService {

    private final DeploymentRepository deploymentRepository;
    private final ServiceRepository serviceRepository;
    private final EnvironmentRepository environmentRepository;

    // UPDATE CONSTRUCTOR TO INJECT NEW REPOSITORIES
    public DeploymentManagementServiceImpl(DeploymentRepository deploymentRepository,
                                           ServiceRepository serviceRepository,
                                           EnvironmentRepository environmentRepository) {
        this.deploymentRepository = deploymentRepository;
        this.serviceRepository = serviceRepository;
        this.environmentRepository = environmentRepository;
    }

    @Override
    public Page<DeploymentResponse> findHistory(Long environmentId, Pageable pageable) {
        Page<Deployment> deployments = environmentId == null
                ? deploymentRepository.findAll(pageable)
                : deploymentRepository.findByEnvironmentId(environmentId, pageable);
        return deployments.map(this::toResponse);
    }

    // IMPLEMENT THE NEW DEPLOYMENT METHOD
    @Override
    @Transactional // Override the class-level readOnly since we are writing to the DB
    public DeploymentResponse deployService(ServiceAssignmentRequest request) {
                Service service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.serviceId()));

        Environment environment = environmentRepository.findById(request.environmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Environment not found with ID: " + request.environmentId()));

        // Inside deployService method:
        Deployment deployment = Deployment.builder()
                .service(service)
                .environment(environment)
                .version(request.version()) // Use request.version() instead of request.getVersion()
                .deploymentStatus(DeploymentStatus.SUCCESS)
                .deploymentTime(LocalDateTime.now())
                .build();

        deployment = deploymentRepository.save(deployment);

        return toResponse(deployment);
    }
    private DeploymentResponse toResponse(Deployment deployment) {
        return new DeploymentResponse(
                deployment.getId(), 
                deployment.getService().getId(), 
                deployment.getService().getName(),
                deployment.getEnvironment().getId(), 
                deployment.getEnvironment().getName(), 
                deployment.getVersion(),
                deployment.getDeploymentStatus(), // <-- Pass the Enum directly here
                deployment.getDeploymentTime()
        );
    }
}