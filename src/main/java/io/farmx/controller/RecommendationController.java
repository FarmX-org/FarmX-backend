package io.farmx.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.farmx.dto.AiDTO;

import io.farmx.service.CropAiService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

	    private final CropAiService cropAiService;

	    public RecommendationController(CropAiService cropAiService) {
	        this.cropAiService = cropAiService;
	    }

	    @PostMapping("/recommend")
	    public ResponseEntity<String> recommendCrop(@RequestBody AiDTO request) {
	        String crop = cropAiService.getRecommendedCrop(request.getSoilType(), request.getSeason());
	        return ResponseEntity.ok(crop);
	    }
	}



