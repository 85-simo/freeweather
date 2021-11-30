package com.example.freeweather.data.api

import com.example.freeweather.data.api.dto.ForecastDTO
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit-specific interface that gives a definition of one category of APIs. In this case, this should contain all APIs used for accessing
 * weather info.
 */

interface RestWeather {
    @GET("/data/2.5/onecall?exclude=minutely,hourly,alerts&units=metric")
    suspend fun getWeatherForecast(@Query("lat") lat: Double, @Query("lon") lon: Double): ForecastDTO
}