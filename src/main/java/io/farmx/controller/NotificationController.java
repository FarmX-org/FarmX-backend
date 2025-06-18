package io.farmx.controller;

import io.farmx.dto.NotificationDTO;
import io.farmx.enums.NotificationType;
import io.farmx.model.Notification;
import io.farmx.model.UserEntity;
import io.farmx.repository.NotificationRepository;
import io.farmx.repository.UserRepository;
import io.farmx.service.NotificationService;
import io.farmx.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {


    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public NotificationController(NotificationRepository notificationRepository,
                                  UserRepository userRepository,
                                  NotificationService notificationService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Notification> notifications = notificationRepository.findByRecipient(user);
        List<NotificationDTO> dtoList = notifications.stream()
                .map(notificationService::toDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/fcm/save-token")
    public ResponseEntity<?> saveFcmToken(Principal principal, @RequestBody Map<String, String> body) {
        String token = body.get("token");
        String username = principal.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFcmToken(token);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Principal principal) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notif.getRecipient().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        notif.setRead(true);
        notificationRepository.save(notif);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/users/{id}/notify")
    public ResponseEntity<?> sendNotificationToUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String title = body.get("title");
        String message = body.get("message");

        UserEntity user = userRepository.findById(id).orElseThrow();
        Notification notif = new Notification();
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setRecipient(user);
        notif.setRead(false);
        notif.setType(NotificationType.INFO); // حسب enum تبعك

        notificationService.saveAndSend(notif);

        return ResponseEntity.ok("Notification sent and saved!");
    }

}
