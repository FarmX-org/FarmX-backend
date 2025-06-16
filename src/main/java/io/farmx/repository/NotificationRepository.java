package io.farmx.repository;

import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipient(UserEntity user, Pageable pageable);
}
