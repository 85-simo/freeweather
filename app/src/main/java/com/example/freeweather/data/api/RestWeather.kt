package com.example.freeweather.data.api

import com.example.freeweather.data.api.dto.ForecastDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RestWeather {
    @GET("/data/2.5/onecall?exclude=minutely,hourly,alerts")
    suspend fun getWeatherForecast(@Query("lat") lat: Double, @Query("lon") lon: Double): ForecastDTO
}