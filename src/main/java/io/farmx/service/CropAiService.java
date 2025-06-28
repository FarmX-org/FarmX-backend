package io.farmx.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CropAiService {

    private final RestTemplate restTemplate;

    @Value("${AI_RECOMMENDER_URL}")
    private String recommenderUrl;

    public CropAiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String getRecommendedCrop(String soilType, String season) {
        Map<String, String> request = new HashMap<>();
        request.put("soil_type", soilType);
        request.put("season", season);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    recommenderUrl + "/recommend", entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody().get("recommended_crop").toString();
            } else {
                return "AI service failed: " + response.getStatusCode();
            }

        } catch (Exception e) {
            return "AI error: " + e.getMessage();
        }
    }
}
