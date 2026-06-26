package com.amazon.seller.devopsportal.repository;

import com.amazon.seller.devopsportal.entity.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    Page<Deployment> findByEnvironmentId(Long environmentId, Pageable pageable);
}
