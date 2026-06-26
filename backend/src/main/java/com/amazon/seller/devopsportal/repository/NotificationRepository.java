package com.amazon.seller.devopsportal.repository;

import com.amazon.seller.devopsportal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReadFalse(Pageable pageable);
    boolean existsByServiceIdAndMessageAndReadFalse(Long serviceId, String message);
}
