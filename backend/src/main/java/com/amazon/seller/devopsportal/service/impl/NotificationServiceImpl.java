package com.amazon.seller.devopsportal.service.impl;

import com.amazon.seller.devopsportal.dto.notification.NotificationResponse;
import com.amazon.seller.devopsportal.entity.Notification;
import com.amazon.seller.devopsportal.entity.NotificationSeverity;
import com.amazon.seller.devopsportal.entity.Service;
import com.amazon.seller.devopsportal.exception.ResourceNotFoundException;
import com.amazon.seller.devopsportal.repository.NotificationRepository;
import com.amazon.seller.devopsportal.repository.ServiceRepository;
import com.amazon.seller.devopsportal.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ServiceRepository serviceRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ServiceRepository serviceRepository) {
        this.notificationRepository = notificationRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAll(boolean unreadOnly, Pageable pageable) {
        Page<Notification> notifications = unreadOnly
                ? notificationRepository.findByReadFalse(pageable)
                : notificationRepository.findAll(pageable);
        return notifications.map(this::toResponse);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification with ID " + notificationId + " was not found"));
        notification.setRead(true);
        return toResponse(notification);
    }

    @Override
    public void createIfNoUnreadDuplicate(Long serviceId, String message, NotificationSeverity severity) {
        if (notificationRepository.existsByServiceIdAndMessageAndReadFalse(serviceId, message)) {
            return;
        }
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service with ID " + serviceId + " was not found"));
        notificationRepository.save(Notification.builder()
                .service(service)
                .message(message)
                .severity(severity)
                .read(false)
                .build());
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getService().getId(), notification.getService().getName(),
                notification.getMessage(), notification.getSeverity(), notification.getCreatedAt(), notification.isRead());
    }
}
