package com.example.freeweather.data.api

import com.example.freeweather.data.api.dto.ForecastDTO
import com.example.freeweather.data.api.dto.LocationInfoDTO
import javax.inject.Inject

/**
 * Interface + concrete implementation of our Rest Client: these will ideally include all API access methods for external callers to use.
 * The 'internal' visibility of the implementation is merely symbolical, as this project consists of a single module only.
 * It is however still useful in conveying the necessity not to directly instantiate the class from outside this layer.
 */
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