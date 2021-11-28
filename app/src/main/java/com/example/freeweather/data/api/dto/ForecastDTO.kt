package com.example.freeweather.data.api.dto

import com.google.gson.annotations.SerializedName

data class ForecastDTO(
    val coord: CoordDTO,
    val weather: List<WeatherDTO>,
    val main: MainDTO,
    val visibility: Double,
    val wind: WindDTO,
    val clouds: CloudsDTO?,
    val rain: RainDTO?,
    val snow: SnowDTO?,
    val dt: Long,
    val sys: SysDTO,
    val timezone: Long,
    val id: Long,
    val name: String
)

data class CoordDTO(
    val lat: Double,
    val lon: Double
)

data class WeatherDTO(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String,
)

data class MainDTO(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    var tempMax: Double,
    var pressure: Double,
    var humidity: Double
)

data class WindDTO(
    val speed: Double,
    val deg: Double
)

data class CloudsDTO(
    val all: Double
)

data class RainDTO(
    @SerializedName("1h")
    val lastHour: Double,
    @SerializedName("3h")
    val lastThreeHrs: Double
)

data class SnowDTO(
    @SerializedName("1h")
    val lastHour: Double,
    @SerializedName("3h")
    val lastThreeHrs: Double
)

data class SysDTO(
    val country: String,
    val sunrise: Long,
    val sunset:  Long
)