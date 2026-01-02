package com.example.weather_service.service;
import com.example.weather_service.dto.WeatherResponse;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherResponse getWeather(double lat, double lon) {

        String url = apiUrl +
                "?lat=" + lat +
                "&lon=" + lon +
                "&units=metric" +
                "&appid=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        Map main = (Map) response.get("main");
        Map wind = (Map) response.get("wind");
        Map weather = ((List<Map>) response.get("weather")).get(0);

        String status = (String) weather.get("main");
        String icon = (String) weather.get("icon");

        return new WeatherResponse(
                (String) response.get("name"),
                ((Number) main.get("temp")).doubleValue(),
                ((Number) main.get("humidity")).intValue(),
                ((Number) wind.get("speed")).doubleValue(),
                status,
                icon,
                plantAdvice(status)
        );
    }

    private String plantAdvice(String status) {
        return switch (status) {
            case "Rain" -> "🌧 Arrosage naturel, pas besoin d'eau aujourd'hui.";
            case "Clear" -> "☀️ Journée sèche, pensez à arroser vos plantes.";
            case "Clouds" -> "⛅ Conditions stables pour la croissance.";
            default -> "🌱 Surveillez vos plantes.";
        };
    }
}


