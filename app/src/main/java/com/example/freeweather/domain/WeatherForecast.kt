package com.example.freeweather.domain

import java.util.*

/**
 * Domain level classes used to provide a data representation that fits the business requirements/logic.
 * By not giving upper layers access to the data-level representation, we minimise the impact that any changes at the data level could
 * have on the rest of the application.
 */

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