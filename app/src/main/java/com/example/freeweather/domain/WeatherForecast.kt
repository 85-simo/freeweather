package com.example.freeweather.domain

import java.util.*

data class WeatherForecast(
    val currentWeather: CurrentWeather,
    val dailyWeather: List<WeatherPrediction>
)

data class CurrentWeather(
    val description: String,
    val iconSmall: String,
    val iconLarge: String,
    val temperature: Float,
    val perceivedTemp: Float,
    val minTemp: Float,
    val maxTemp: Float,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val windSpeed: Float,
    val windAngle: Int,
    val timestamp: Date,
    val sunrise: Date,
    val sunset: Date
)

data class WeatherPrediction(
    val description: String,
    val iconSmall: String,
    val iconLarge: String,
    val minTemp: Float,
    val maxTemp: Float,
    val timestamp: Date
)