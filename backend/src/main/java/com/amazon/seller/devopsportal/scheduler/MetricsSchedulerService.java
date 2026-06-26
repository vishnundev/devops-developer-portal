package com.amazon.seller.devopsportal.scheduler;

import com.amazon.seller.devopsportal.entity.Metric;
import com.amazon.seller.devopsportal.entity.NotificationSeverity;
import com.amazon.seller.devopsportal.entity.Service;
import com.amazon.seller.devopsportal.entity.ServiceStatus;
import com.amazon.seller.devopsportal.repository.MetricRepository;
import com.amazon.seller.devopsportal.repository.ServiceRepository;
import com.amazon.seller.devopsportal.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class MetricsSchedulerService {

    private static final double ALERT_THRESHOLD_PERCENT = 90.0;

    private final ServiceRepository serviceRepository;
    private final MetricRepository metricRepository;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;

    public MetricsSchedulerService(ServiceRepository serviceRepository,
                                   MetricRepository metricRepository, 
                                   NotificationService notificationService) {
        this.serviceRepository = serviceRepository;
        this.metricRepository = metricRepository;
        this.notificationService = notificationService;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRateString = "${monitoring.metrics.snapshot-interval-ms:60000}")
    @Transactional
    public void captureMetrics() {
        System.out.println("⏰ Scheduler woke up! Fetching metrics from services...");
        List<Service> services = serviceRepository.findAll();
        LocalDateTime capturedAt = LocalDateTime.now();

        for (Service service : services) {
            // Skip if user intentionally stopped it in UI
            if (service.getStatus() == ServiceStatus.STOPPED) continue;

            try {
                Snapshot snapshot = fetchSnapshotFromService(service);
                
                Metric metric = Metric.builder()
                        .service(service)
                        .cpuUsage(snapshot.cpuUsage())
                        .memoryUsage(snapshot.memoryUsage())
                        .requestCount(snapshot.requestCount())
                        .errorCount(snapshot.errorCount())
                        .uptime(snapshot.uptimeSeconds())
                        .capturedAt(capturedAt)
                        .build();
                metricRepository.save(metric);

                // Auto-Recovery: If it was down, mark it as running again!
                if (service.getStatus() != ServiceStatus.RUNNING) {
                    service.setStatus(ServiceStatus.RUNNING);
                    serviceRepository.save(service);
                    notificationService.createIfNoUnreadDuplicate(service.getId(), 
                            "RECOVERY: " + service.getName() + " is back online.", 
                            NotificationSeverity.INFO);
                }

                createAlerts(service, snapshot);

            } catch (Exception e) {
                // Auto-Crash Detection: If call fails, mark as DOWN in database!
                if (service.getStatus() != ServiceStatus.DOWN) {
                    service.setStatus(ServiceStatus.DOWN);
                    serviceRepository.save(service);
                    
                    notificationService.createIfNoUnreadDuplicate(service.getId(), 
                            "CRITICAL ALERT: Cannot reach " + service.getName() + " on port " + service.getPort() + ".",
                            NotificationSeverity.CRITICAL);
                }
            }
        }
    }

    private Snapshot fetchSnapshotFromService(Service service) {
        double cpuUsage = clampToPercentage(fetchActuatorValue(service, "system.cpu.usage") * 100.0);
        double usedHeap = fetchActuatorValue(service, "jvm.memory.used");
        double maxHeap = fetchActuatorValue(service, "jvm.memory.max");
        double memoryUsage = maxHeap > 0 ? clampToPercentage((usedHeap / maxHeap) * 100.0) : 0.0;
        long requestCount = (long) fetchActuatorValue(service, "http.server.requests");
        long uptimeSeconds = Math.max(0L, Math.round(fetchActuatorValue(service, "process.uptime")));

        return new Snapshot(toDecimal(cpuUsage), toDecimal(memoryUsage), requestCount, 0, uptimeSeconds);
    }

    private double fetchActuatorValue(Service service, String metricName) {
        try {
            String url = "http://localhost:" + service.getPort() + "/actuator/metrics/" + metricName;
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && response.has("measurements")) {
                return response.get("measurements").get(0).get("value").asDouble();
            }
        } catch (HttpClientErrorException.NotFound e) {
            return 0.0;
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Service unreachable", e); // Triggers the DOWN alert
        } catch (Exception e) {
            return 0.0;
        }
        return 0.0;
    }

    private void createAlerts(Service service, Snapshot snapshot) {
        if (snapshot.cpuUsage().doubleValue() >= ALERT_THRESHOLD_PERCENT) {
            notificationService.createIfNoUnreadDuplicate(service.getId(), "High CPU usage: " + snapshot.cpuUsage() + "%", NotificationSeverity.WARNING);
        }
        if (snapshot.memoryUsage().doubleValue() >= ALERT_THRESHOLD_PERCENT) {
            notificationService.createIfNoUnreadDuplicate(service.getId(), "High memory usage: " + snapshot.memoryUsage() + "%", NotificationSeverity.WARNING);
        }
    }

    private BigDecimal toDecimal(double value) { return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP); }
    private double clampToPercentage(double value) { return Math.max(0.0, Math.min(100.0, value)); }
    private record Snapshot(BigDecimal cpuUsage, BigDecimal memoryUsage, long requestCount, long errorCount, long uptimeSeconds) {}
}