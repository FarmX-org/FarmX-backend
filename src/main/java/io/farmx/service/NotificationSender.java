package io.farmx.service;

import io.farmx.dto.NotificationDTO;
import io.farmx.model.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationSender {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotificationToUser(Long userId, Notification notification) {
        String destination = "/topic/notifications-user-" + userId;
        NotificationDTO dto = new NotificationDTO(
            notification.getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getType(),
            notification.isRead(),
            notification.getCreatedAt(),
            notification.getRecipient() != null ? notification.getRecipient().getId() : null
        );
        messagingTemplate.convertAndSend(destination, dto);
    }

}
