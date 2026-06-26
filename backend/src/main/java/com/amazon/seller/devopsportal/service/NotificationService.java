package com.amazon.seller.devopsportal.service;

import com.amazon.seller.devopsportal.dto.notification.NotificationResponse;
import com.amazon.seller.devopsportal.entity.NotificationSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationResponse> findAll(boolean unreadOnly, Pageable pageable);
    NotificationResponse markAsRead(Long notificationId);
    void createIfNoUnreadDuplicate(Long serviceId, String message, NotificationSeverity severity);
}
