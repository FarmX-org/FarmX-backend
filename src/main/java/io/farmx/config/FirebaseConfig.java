package io.farmx.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CREDENTIALS:}")
    private String firebaseEnvCredentials;

    @PostConstruct
    public void initializeFirebase() throws IOException {
        InputStream serviceAccount;

        if (!firebaseEnvCredentials.isEmpty()) {
            // running on Render, decode string
            byte[] decoded = Base64.getDecoder().decode(firebaseEnvCredentials);
            serviceAccount = new ByteArrayInputStream(decoded);
            System.out.println("🔐 Firebase loaded from ENV variable");
        } else {
            // running locally, use JSON file
            serviceAccount = new FileInputStream("src/main/resources/firebase/serviceAccountKey.json");
            System.out.println("🧪 Firebase loaded from local file");
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
