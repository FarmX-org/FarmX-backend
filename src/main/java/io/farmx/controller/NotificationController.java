package io.farmx.controller;

import io.farmx.dto.NotificationDTO;
import io.farmx.model.Notification;
import io.farmx.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;


    public NotificationController(NotificationService service) {
        this.service = service;
    }


    @GetMapping
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
    }
}
