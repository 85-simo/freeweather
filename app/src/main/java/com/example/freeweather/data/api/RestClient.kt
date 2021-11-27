package com.example.freeweather.data.api

import com.example.freeweather.data.api.dto.ForecastDTO
import com.example.freeweather.data.api.dto.LocationInfoDTO
import javax.inject.Inject

interface RestClient {
    suspend fun getWeatherForecast(lat: Double, lon: Double): ForecastDTO
    suspend fun getCityLatLon(cityName: String): List<LocationInfoDTO>
}

internal class RestClientImpl @Inject constructor(
    private val weather: RestWeather,
    private val geocoding: RestGeocoding
    ) : RestClient {

    override suspend fun getWeatherForecast(lat: Double, lon: Double) = weather.getWeatherForecast(lat, lon)

    override suspend fun getCityLatLon(cityName: String) = geocoding.getCityLatLon(cityName)

}