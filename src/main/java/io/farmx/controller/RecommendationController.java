package io.farmx.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.farmx.model.Crop;
import io.farmx.model.Farm;
import io.farmx.repository.FarmRepository;
import io.farmx.service.RecommendationService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final FarmRepository farmRepository;

    public RecommendationController(RecommendationService recommendationService, FarmRepository farmRepository) {
        this.recommendationService = recommendationService;
        this.farmRepository = farmRepository;
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<Crop>> getRecommendations(@PathVariable Long farmId) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));
        List<Crop> recommendedCrops = recommendationService.recommendCropsForFarm(farm);
        return ResponseEntity.ok(recommendedCrops);
    }
}

