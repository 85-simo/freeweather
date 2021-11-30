package com.example.freeweather.data.repository

import com.example.freeweather.data.api.RestClient
import com.example.freeweather.data.api.dto.ForecastDTO
import com.example.freeweather.data.api.dto.LocationInfoDTO
import com.example.freeweather.data.db.AppDatabase
import com.example.freeweather.data.db.entities.FavouriteCity
import com.example.freeweather.domain.City
import com.example.freeweather.domain.CurrentWeather
import com.example.freeweather.domain.WeatherForecast
import com.example.freeweather.domain.WeatherPrediction
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Repository implementation to provide a point of contact between the data layer and the layers that sit above - in this case, the presentation layer.
 * This aims to expose all business-level operations to the upper layers while internally handling the complexities of data retrieval and manipulation.
 * Every public method must return a business-level representation of data, in order to limit the impact of changes that may occur in this layer.
 * The 'internal' visibility of the implementation is merely symbolical, as this project consists of a single module only.
 * It is however still useful in conveying the necessity not to directly instantiate the class from outside this layer.
 */

interface Repository {
    suspend fun getCitiesByName(cityName: String): List<City>
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): WeatherForecast
    suspend fun saveFavouriteCity(city: City)
    suspend fun getFavouriteCities(): List<City>
    suspend fun deleteFavouriteCity(city: City)
    suspend fun getFavouriteCityByCoordinates(latitude: Double, longitude: Double): City?
}

internal class RepositoryImpl @Inject constructor(
    private val restClient: RestClient,
    private val dbClient: AppDatabase
    ) : Repository {

    override suspend fun getCitiesByName(cityName: String) = restClient.getCityLatLon(cityName).map { it.toDomain() }

    override suspend fun getWeatherByCoordinates(lat: Double, lon: Double) = restClient.getWeatherForecast(lat, lon).toDomain()

    override suspend fun saveFavouriteCity(city: City) {
        dbClient.favouriteCityDao().insert(city.toData())
    }

    override suspend fun getFavouriteCities() = dbClient.favouriteCityDao().getAll().map { city ->
        city.toDomain()
    }

    override suspend fun deleteFavouriteCity(city: City) = dbClient.favouriteCityDao().delete(city.toData())

    override suspend fun getFavouriteCityByCoordinates(latitude: Double, longitude: Double) = dbClient.favouriteCityDao().getFavouriteCityByCoordinates(latitude, longitude)?.toDomain()

}

private fun LocationInfoDTO.toDomain() = City(
    name = name,
    state = state,
    country = country,
    latitude = lat,
    longitude = lon
)

private fun ForecastDTO.toDomain() = WeatherForecast(
    currentWeather = CurrentWeather(
        description = current.weather[0].description,
        iconSmall = "https://openweathermap.org/img/wn/${current.weather[0].icon}@2x.png",
        iconLarge = "https://openweathermap.org/img/wn/${current.weather[0].icon}@4x.png",
        temperature = current.temp,
        perceivedTemp = current.feelsLike,
        minTemp = daily[0].temp.min,
        maxTemp = daily[0].temp.max,
        pressure = current.pressure,
        humidity = current.humidity,
        visibility = current.visibility,
        windSpeed = current.windSpeed,
        windAngle = current.windDeg,
        timestamp = Date(TimeUnit.SECONDS.toMillis(current.dtSeconds)),
        sunrise = Date(TimeUnit.SECONDS.toMillis(current.sunriseSeconds)),
        sunset = Date(TimeUnit.SECONDS.toMillis(current.sunsetSeconds))
    ),
    dailyWeather = daily.map {
        WeatherPrediction(
            description = it.weather[0].description,
            iconSmall = "https://openweathermap.org/img/wn/${it.weather[0].icon}@2x.png",
            iconLarge = "https://openweathermap.org/img/wn/${it.weather[0].icon}@4x.png",
            minTemp = it.temp.min,
            maxTemp = it.temp.max,
            timestamp = Date(TimeUnit.SECONDS.toMillis(it.dtSeconds))
        )
    }
)

private fun FavouriteCity.toDomain() = City(name, state, country, latitude, longitude)

private fun City.toData() = FavouriteCity(name, state, country, latitude, longitude)