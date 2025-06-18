package io.farmx.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.farmx.dto.TokenRequestDTO;
import io.farmx.model.UserEntity;
import io.farmx.repository.UserRepository;
import io.farmx.service.FCMService;
import io.farmx.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/notifications")
public class NotificationFCMController {

	    @Autowired
	    private FCMService fcmService;
	    @Autowired
		private UserService userService;
	    @Autowired
		private UserRepository userRepository;

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
	        System.out.println("👤 Principal username: " + username);

	        UserEntity user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new IllegalArgumentException("User not found"));

	        String token = tokenRequest.getFcmToken();
	        System.out.println("📥 Received FCM Token: " + token); // ✅ DEBUG HERE

	        if (token == null || token.isBlank()) {
	            return ResponseEntity.badRequest().body("❌ Token is null or blank");
	        }

	        user.setFcmToken(token);
	        userRepository.save(user);

	        System.out.println("💾 Token saved for user: " + username);

	        return ResponseEntity.ok("Token saved");
	    }




	}
