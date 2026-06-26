package com.amazon.seller.devopsportal.controller;

import com.amazon.seller.devopsportal.dto.ApiResponse;
import com.amazon.seller.devopsportal.dto.service.ServiceRequest;
import com.amazon.seller.devopsportal.dto.service.ServiceResponse;
import com.amazon.seller.devopsportal.dto.service.ServiceStatusRequest;
import com.amazon.seller.devopsportal.service.ServiceManagementService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
@Validated
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    public ServiceController(ServiceManagementService serviceManagementService) {
        this.serviceManagementService = serviceManagementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ServiceResponse>>> findAll(@PageableDefault(sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Services retrieved successfully", serviceManagementService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ApiResponse.success("Service retrieved successfully", serviceManagementService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> create(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service created successfully", serviceManagementService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> update(@PathVariable @Positive Long id,
                                                                @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service updated successfully", serviceManagementService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    public ResponseEntity<ApiResponse<ServiceResponse>> changeStatus(@PathVariable @Positive Long id,
                                                                       @Valid @RequestBody ServiceStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Service status updated successfully",
                serviceManagementService.changeStatus(id, request.action())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @Positive Long id) {
        serviceManagementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Service deleted successfully", null));
    }
}
