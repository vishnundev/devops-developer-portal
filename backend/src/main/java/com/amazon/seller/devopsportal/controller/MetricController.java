package com.amazon.seller.devopsportal.controller;

import com.amazon.seller.devopsportal.dto.ApiResponse;
import com.amazon.seller.devopsportal.entity.Metric;
import com.amazon.seller.devopsportal.repository.MetricRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin("*")
public class MetricController {

    private final MetricRepository metricRepository;

    public MetricController(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllMetrics() {

        try {

            List<Metric> metrics = metricRepository.findAllWithService();

            List<Map<String, Object>> response = metrics.stream().map(metric -> {

                Map<String, Object> map = new HashMap<>();

                map.put("id", metric.getId());

                map.put("cpuUsage", metric.getCpuUsage());

                map.put("memoryUsage", metric.getMemoryUsage());

                map.put("uptime", metric.getUptime());

                map.put("requestCount", metric.getRequestCount());

                map.put("errorCount", metric.getErrorCount());

                map.put("capturedAt", metric.getCapturedAt());

                map.put("serviceName", metric.getService().getName());

                return map;

            }).collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Metrics fetched successfully",
                            response
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().body(e.getMessage());

        }
    }
}