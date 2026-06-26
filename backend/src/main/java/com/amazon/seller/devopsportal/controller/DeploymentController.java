package com.amazon.seller.devopsportal.controller;

import com.amazon.seller.devopsportal.dto.ApiResponse;
import com.amazon.seller.devopsportal.dto.deployment.DeploymentHistoryFilter;
import com.amazon.seller.devopsportal.dto.deployment.DeploymentResponse;
import com.amazon.seller.devopsportal.service.DeploymentManagementService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.amazon.seller.devopsportal.dto.environment.ServiceAssignmentRequest;
@RestController
@RequestMapping("/api/deployments")
@Validated
public class DeploymentController {

    private final DeploymentManagementService deploymentManagementService;

    public DeploymentController(DeploymentManagementService deploymentManagementService) {
        this.deploymentManagementService = deploymentManagementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    public ResponseEntity<ApiResponse<Page<DeploymentResponse>>> findHistory(
            @Valid @ModelAttribute DeploymentHistoryFilter filter,
            @PageableDefault(sort = "deploymentTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Deployment history retrieved successfully",
                deploymentManagementService.findHistory(filter.environmentId(), pageable)));
    }
    
   // Add this inside backend/src/main/java/com/amazon/seller/devopsportal/controller/DeploymentController.java


// Inside the class:
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    public ResponseEntity<ApiResponse<DeploymentResponse>> triggerDeployment(
            @Valid @RequestBody ServiceAssignmentRequest request) { // Assuming request has serviceId, environmentId, version
        
        // Call your service layer here. If your service layer doesn't have this yet, 
        // it needs a method to create and save a new Deployment entity.
        DeploymentResponse response = deploymentManagementService.deployService(request); 
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deployment triggered successfully", response));
    } 
}
