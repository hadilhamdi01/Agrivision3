package com.example.weather_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class WeatherResponse {
    private String location;
    private double temperature;
    private int humidity;
    private double windSpeed;
    private String status;
    private String icon;
    private String plantAdvice;
}
