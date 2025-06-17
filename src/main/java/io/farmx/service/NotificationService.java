package io.farmx.service;

import io.farmx.dto.NotificationDTO;
import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import io.farmx.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification saveAndSend(Notification notification) {
        Notification saved = notificationRepository.save(notification);

        // Send via WebSocket to the user’s private queue
        String destination = "/user/" + notification.getRecipient().getUsername() + "/queue/notifications";
        messagingTemplate.convertAndSendToUser(
                notification.getRecipient().getUsername(),
                "/queue/notifications",
                toDTO(saved)
        );
        System.out.println("Sending notification to user: " + notification.getRecipient().getUsername());
        System.out.println("Notification message: " + notification.getMessage());


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
