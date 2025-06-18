package io.farmx.service;

import io.farmx.dto.NotificationDTO;
import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import io.farmx.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import io.farmx.dto.NotificationDTO;
import io.farmx.model.Notification;
import io.farmx.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FirebaseService firebaseService;

    public Notification saveAndSend(Notification notification) {
        Notification saved = notificationRepository.save(notification);

        String fcmToken = notification.getRecipient().getFcmToken();
        if (fcmToken != null && !fcmToken.isBlank()) {
            firebaseService.sendNotificationToToken(
                notification.getTitle(),
                notification.getMessage(),
                fcmToken
            );
        }

        return saved;
    }


    public NotificationDTO toDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getRecipient().getId()
        );
    }
}

