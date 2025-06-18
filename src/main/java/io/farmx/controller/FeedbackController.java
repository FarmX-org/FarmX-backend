package io.farmx.controller;

import io.farmx.dto.FeedbackDTO;
import io.farmx.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {


    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> giveFeedback(@RequestBody FeedbackDTO dto, Principal principal) {
        feedbackService.submitFeedback(dto, principal);
        return ResponseEntity.ok("Feedback submitted successfully");
    }
    
    @GetMapping("/farmer")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksForFarmerFarms(Principal principal) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksForFarmer(principal);
        return ResponseEntity.ok(feedbacks);
    }

  @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }
}
