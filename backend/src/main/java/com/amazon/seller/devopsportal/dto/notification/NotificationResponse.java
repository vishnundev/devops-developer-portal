package com.amazon.seller.devopsportal.dto.notification;

import com.amazon.seller.devopsportal.entity.NotificationSeverity;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, Long serviceId, String serviceName, String message,
                                   NotificationSeverity severity, LocalDateTime createdAt, boolean read) {
}
