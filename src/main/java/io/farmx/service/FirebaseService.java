package io.farmx.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FirebaseService {

    private final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final String SERVER_KEY = "Bearer AAAA..."; // حط مفتاحك من Firebase Console

    public void sendNotificationToToken(String title, String message, String token) {
        try {
            var body = new HashMap<String, Object>();
            body.put("to", token);

            Map<String, String> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", message);
            body.put("notification", notification);

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(FIREBASE_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", SERVER_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(body)))
                    .build();

            var client = HttpClient.newHttpClient();
            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
