package com.amazon.seller.devopsportal.repository;

import com.amazon.seller.devopsportal.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long> {

    @Query("SELECT m FROM Metric m JOIN FETCH m.service")
    List<Metric> findAllWithService();

}