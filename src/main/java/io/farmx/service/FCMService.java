package io.farmx.service;


import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FCMService {
	
	public String sendNotificationToToken(String title, String body, String token) throws FirebaseMessagingException {
		Message message = Message.builder()
			    .setToken(token)
			    .putData("title", "Test title")
			    .putData("body", "Test body content")
			    .build();

	    return FirebaseMessaging.getInstance().send(message);
	}


  /*  public String sendNotificationToToken(String title, String body, String token) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }*/

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
