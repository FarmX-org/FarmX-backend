package io.farmx.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${OPENWEATHER_API_KEY}")
    private String apiKey;

    public String getWeatherData(double lat, double lon) {
    	String url = String.format(
    		    "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric",
    		    lat, lon, apiKey
    		);
         return restTemplate.getForObject(url, String.class);
    }
}
