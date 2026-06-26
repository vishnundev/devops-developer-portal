package com.amazon.seller.devopsportal.controller;

import com.amazon.seller.devopsportal.dto.ApiResponse;
import com.amazon.seller.devopsportal.dto.environment.EnvironmentRequest;
import com.amazon.seller.devopsportal.dto.environment.EnvironmentResponse;
import com.amazon.seller.devopsportal.dto.environment.ServiceAssignmentRequest;
import com.amazon.seller.devopsportal.service.EnvironmentManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/environments")
@Validated
public class EnvironmentController {

    private final EnvironmentManagementService environmentManagementService;

    public EnvironmentController(EnvironmentManagementService environmentManagementService) {
        this.environmentManagementService = environmentManagementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EnvironmentResponse>>> findAll(@PageableDefault(sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Environments retrieved successfully",
                environmentManagementService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ApiResponse.success("Environment retrieved successfully",
                environmentManagementService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> create(@Valid @RequestBody EnvironmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Environment created successfully", environmentManagementService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> update(@PathVariable @Positive Long id,
                                                                    @Valid @RequestBody EnvironmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Environment updated successfully",
                environmentManagementService.update(id, request)));
    }

    @PostMapping("/{id}/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> assignService(@PathVariable @Positive Long id,
                                                                            @Valid @RequestBody ServiceAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service assigned to environment successfully",
                environmentManagementService.assignService(id, request.serviceId())));
    }

    @DeleteMapping("/{id}/services/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> removeService(@PathVariable @Positive Long id,
                                                                            @PathVariable @Positive Long serviceId) {
        return ResponseEntity.ok(ApiResponse.success("Service removed from environment successfully",
                environmentManagementService.removeService(id, serviceId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @Positive Long id) {
        environmentManagementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Environment deleted successfully", null));
    }
}
