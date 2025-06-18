package io.farmx.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.farmx.service.FirebaseMessagingService;

@RestController
@RequestMapping("/test")
public class NotificationTestController {

    private final FirebaseMessagingService messagingService;

    public NotificationTestController(FirebaseMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/notify")
    public ResponseEntity<?> sendTestNotification(@RequestParam String token) {
        try {
            messagingService.sendNotification(token, "Hello Frontend!", "This is from your Spring Boot backend 🎯");
            return ResponseEntity.ok("Notification sent!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
