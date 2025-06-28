package io.farmx.controller;

import io.farmx.service.WeatherService;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public ResponseEntity<String> getWeather(
            @RequestParam double lat,
            @RequestParam double lon) {

        String weatherJson = weatherService.getWeatherData(lat, lon);
        JSONObject obj = new JSONObject(weatherJson);

        double temp = obj.getJSONObject("main").getDouble("temp");
        String weatherDescription = obj.getJSONArray("weather").getJSONObject(0).getString("main"); // "Clear", "Clouds", etc.

        JSONObject response = new JSONObject();
        response.put("temperature", temp);
        response.put("condition", weatherDescription);

        // Optional: add a friendly message
        String message = switch (weatherDescription.toLowerCase()) {
            case "clear" -> "It's sunny today!";
            case "clouds" -> "Cloudy skies ahead.";
            case "rain" -> "Expect some rain, don't forget your umbrella!";
            case "snow" -> "Snowfall expected, stay warm!";
            case "fog" -> "Foggy weather, drive carefully!";
            default -> "Weather looks normal.";
        };

        response.put("message", message);

        return ResponseEntity.ok(response.toString());
    }
}
