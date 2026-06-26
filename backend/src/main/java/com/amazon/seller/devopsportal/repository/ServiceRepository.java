package com.amazon.seller.devopsportal.repository;

import com.amazon.seller.devopsportal.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    boolean existsByNameIgnoreCase(String name);
}
