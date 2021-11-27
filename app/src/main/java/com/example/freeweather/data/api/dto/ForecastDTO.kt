package com.example.freeweather.data.api.dto

import com.google.gson.annotations.SerializedName

data class ForecastDTO(
    val coord: Coord?,
    val weather: List<Weather>?,
    val main: Main?,
    val visibility: Double?,
    val wind: Wind?,
    val clouds: Clouds?,
    val dt: Long?,
    val sys: Sys?,
    val timezone: Long?,
    val id: Long?,
    val name: String?
)

data class Coord(
    val lon: Double?,
    val lat: Double?
)

data class Weather(
    val id: Long?,
    val main: String?,
    val description: String?,
    val icon: String?,
)

data class Main(
    val temp: Double?,
    @SerializedName("feels_like")
    val feelsLike: Double?,
    @SerializedName("temp_min")
    val tempMin: Double?,
    @SerializedName("temp_max")
    var tempMax: Double?,
    var pressure: Double?,
    var humidity: Double?
)

data class Wind(
    val speed: Double?,
    val deg: Double?
)

data class Clouds(
    val all: Double?
)

data class Rain(
    @SerializedName("1h")
    val lastHour: Double?,
    @SerializedName("3h")
    val h: Double?
)

data class Sys(
    val country: String?,
    val sunrise: Long?,
    val sunset:  Long?
)