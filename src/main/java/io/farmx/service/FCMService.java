// --- FCMService.java ---
package io.farmx.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public String sendNotificationToToken(String title, String body, String token) throws FirebaseMessagingException {
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
            .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    public String sendBroadcastNotification(String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setTopic("all")
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }
}