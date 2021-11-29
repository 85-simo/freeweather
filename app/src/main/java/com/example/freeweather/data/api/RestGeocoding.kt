package com.example.freeweather.data.api

import com.example.freeweather.data.api.dto.LocationInfoDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RestGeocoding {
    @GET("/geo/1.0/direct?limit=5")
    suspend fun getCityLatLon(@Query("q") cityName: String): List<LocationInfoDTO>
}