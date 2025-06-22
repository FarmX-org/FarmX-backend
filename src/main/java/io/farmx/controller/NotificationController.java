package io.farmx.controller;

import io.farmx.dto.NotificationDTO;
import io.farmx.model.Notification;
import io.farmx.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;


    public NotificationController(NotificationService service) {
        this.service = service;
    }


    @GetMapping
    public List<NotificationDTO> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        return service.getUserNotifications(principal);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        service.markAllAsRead(principal);
        return ResponseEntity.ok().build();
    }
}
