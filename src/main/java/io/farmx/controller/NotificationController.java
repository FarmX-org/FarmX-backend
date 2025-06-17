package io.farmx.controller;

import io.farmx.dto.NotificationDTO;
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
    @MessageMapping("/sendMessage") // Endpoint matching the JavaScript destination
    @SendTo("/topic/notifications") // Broadcast to subscribers of this topic
    public String sendMessage(String message) {
        System.out.println("Received message: " + message); // Debugging log
        return message; // Broadcast the message
    }

    /*@GetMapping
    public Page<Notification> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        return service.getNotifications(principal, page, size);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        service.markAsRead(id, principal);
        return ResponseEntity.ok().build();
    }*/
}
