package io.farmx.service;


import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FCMService {
	
	public String sendNotificationToToken(String title, String body, String token) throws FirebaseMessagingException {

		Message message = Message.builder()
		    .setToken(token) // The user's FCM token
		    .setNotification(Notification.builder()
		        .setTitle("New User Added!")
		        .setBody("A new user has registered on the platform.")
		        .build())
		    .putData("userId", "12345")
		    .putData("action", "newUser")
		    .build();

		try {
		    String response = FirebaseMessaging.getInstance().send(message);
		    System.out.println("Successfully sent message: " + response);
		} catch (Exception e) {
		    // Log the full exception to understand the error
		    e.printStackTrace();
		}
		
	
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
