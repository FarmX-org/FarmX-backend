// --- NotificationController.java ---
package io.farmx.controller;

import io.farmx.dto.NotificationDTO;
import io.farmx.dto.TokenRequestDTO;
import io.farmx.model.UserEntity;
import io.farmx.repository.UserRepository;
import io.farmx.service.FCMService;
import io.farmx.service.NotificationService;
import io.farmx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationFCMController {

    @Autowired
    private FCMService fcmService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;
    @Autowired
	private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(Principal principal) {
        return ResponseEntity.ok(notificationService.getUserNotifications(principal));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<String> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(principal);
        return ResponseEntity.ok("All notifications marked as read");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendToUser(@RequestParam String token,
                                             @RequestParam String title,
                                             @RequestParam String message) {
        try {
            String response = fcmService.sendNotificationToToken(title, message, token);
            return ResponseEntity.ok("Sent successfully: " + response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcast(@RequestParam String title,
                                            @RequestParam String message) {
        try {
            String response = fcmService.sendBroadcastNotification(title, message);
            return ResponseEntity.ok("Broadcasted: " + response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/save-token")
    public ResponseEntity<String> saveToken(@RequestBody TokenRequestDTO tokenRequest,
                                            Principal principal) {
    	String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        String token = tokenRequest.getFcmToken();
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("Token is null or blank");
        }

        user.setFcmToken(token);
        userRepository.save(user);

        return ResponseEntity.ok("Token saved");
    }
}
