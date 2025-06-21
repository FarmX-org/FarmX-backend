
package io.farmx.service;

import io.farmx.dto.NotificationDTO;
import io.farmx.enums.NotificationType;
import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import io.farmx.repository.NotificationRepository;
import io.farmx.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;


@Service
public class NotificationService {


    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<NotificationDTO> getUserNotifications(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return notificationRepository.findByRecipient(user)
                .stream()
                .map(n -> new NotificationDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getType(),
                        n.isRead(),
                        n.getCreatedAt(),
                        n.getRecipient().getId()
                ))
                .collect(Collectors.toList());
    }

    public void saveNotification(UserEntity recipient, String title, String message, NotificationType type) {
       Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Notification> notifications = notificationRepository.findByRecipient(user);
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
            }
        }
        notificationRepository.saveAll(notifications);
    }

}
