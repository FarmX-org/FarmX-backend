package io.farmx.service;

import io.farmx.dto.NotificationDTO;
import io.farmx.enums.NotificationType;
import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import io.farmx.repository.NotificationRepository;
import io.farmx.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    private final NotificationSender notificationSender;

    public NotificationService(NotificationRepository notificationRepo, UserRepository userRepo, NotificationSender notificationSender) {
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
        this.notificationSender = notificationSender;
    }

    public Page<Notification> getNotifications(Principal principal, int page, int size) {
        UserEntity user = userRepo.findByUsername(principal.getName()).orElseThrow();
        return notificationRepo.findByRecipient(user, PageRequest.of(page, size));
    }

    public void markAsRead(Long id, Principal principal) {
        Notification notif = notificationRepo.findById(id).orElseThrow();
        if (!notif.getRecipient().getUsername().equals(principal.getName())) {
            throw new SecurityException("Not your notification!");
        }
        notif.setRead(true);
        notificationRepo.save(notif);
    }

    public void sendNotificationTo(UserEntity recipient, String title, String msg, NotificationType type) {
        Notification notif = new Notification();
        notif.setTitle(title);
        notif.setMessage(msg);
        notif.setType(type);
        notif.setRecipient(recipient);
        notificationRepo.save(notif);
        notificationSender.sendNotificationToUser(recipient.getId(), notif);
    }
    
    public NotificationDTO toDTO(Notification notification) {
        return new NotificationDTO(
            notification.getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getType(),
            notification.isRead(),
            notification.getCreatedAt(),
            notification.getRecipient() != null ? notification.getRecipient().getId() : null
        );
    }


}
