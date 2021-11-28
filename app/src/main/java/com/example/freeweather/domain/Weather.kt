package com.example.freeweather.domain

data class Weather(
    val short: String,
    val description: String,
    val iconSmall: String,
    val iconLarge: String,
    val temperature: Double,
    val perceivedTemp: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val pressure: Double,
    val humidity: Double,
    val visibility: Double,
    val windSpeed: Double,
    val windAngle: Double,
    val updatedAt: Long,
    val cityName: String,
    val country: String
)