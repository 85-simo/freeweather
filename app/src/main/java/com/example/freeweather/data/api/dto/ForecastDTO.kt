package com.example.freeweather.data.api.dto

import com.google.gson.annotations.SerializedName

data class WeatherDTO(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class RainDTO(
    @SerializedName("1h")
    val lastHour: Float?
)

data class SnowDTO(
    @SerializedName("1h")
    val lastHour: Float?
)

data class CurrentDTO(
    @SerializedName("dt")
    val dtSeconds: Long,
    @SerializedName("sunrise")
    val sunriseSeconds: Long,
    @SerializedName("sunset")
    val sunsetSeconds: Long,
    val temp: Float,
    @SerializedName("feels_like")
    val feelsLike: Float,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Float,
    val uvi: Float,
    val clouds: Int,
    val visibility: Int,
    @SerializedName("wind_speed")
    val windSpeed: Float,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Float?,
    val weather: List<WeatherDTO>,
    val rain: RainDTO?,
    val snow: SnowDTO?
)

data class TempDTO(
    val day: Float,
    val min: Float,
    val max: Float,
    val night: Float,
    val eve: Float,
    val morn: Float
)

data class FeelsLikeDTO(
    val day: Float,
    val night: Float,
    val eve: Float,
    val morn: Float
)

data class DailyDTO(
    @SerializedName("dt")
    val dtSeconds: Long,
    @SerializedName("sunrise")
    val sunriseSeconds: Long,
    @SerializedName("sunset")
    val sunsetSeconds: Long,
    @SerializedName("moonrise")
    val moonriseSeconds: Long,
    @SerializedName("moonset")
    val moonsetSeconds: Long,
    @SerializedName("moon_phase")
    val moonPhase: Float,
    val temp: TempDTO,
    @SerializedName("feels_like")
    val feelsLike: FeelsLikeDTO,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Float,
    @SerializedName("wind_speed")
    val windSpeed: Float,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_gust")
    val windGust: Float?,
    val weather: List<WeatherDTO>,
    val clouds: Int,
    val pop: Float,
    val rain: Float?,
    val uvi: Float,
    val snow: Float?
)

data class ForecastDTO(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffsetSeconds: Long,
    val current: CurrentDTO,
    var daily: List<DailyDTO>
)
