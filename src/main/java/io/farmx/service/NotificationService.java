// io.farmx.service.NotificationService.java
package io.farmx.service;

import io.farmx.model.Notification;
import io.farmx.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    public NotificationService(NotificationRepository repo) { this.repo = repo; }
    public void send(Long userId, String msg) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(msg);
        repo.save(n);
        // you could push via WebSocket here…
    }
    public List<Notification> getForUser(Long userId) {
        return repo.findByUserIdOrderByTimestampDesc(userId);
    }
}
