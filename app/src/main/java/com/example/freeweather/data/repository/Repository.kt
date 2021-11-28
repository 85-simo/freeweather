package com.example.freeweather.data.repository

import com.example.freeweather.data.api.RestClient
import com.example.freeweather.data.api.dto.ForecastDTO
import com.example.freeweather.data.api.dto.LocationInfoDTO
import com.example.freeweather.data.db.AppDatabase
import com.example.freeweather.data.db.entities.FavouriteCity
import com.example.freeweather.domain.City
import com.example.freeweather.domain.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface Repository {
    suspend fun getCitiesByName(cityName: String): List<City>
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Weather
    suspend fun saveFavouriteCity(city: City)
    fun getFavouriteCities(): Flow<List<City>>
    suspend fun deleteFavouriteCity(city: City)
}

internal class RepositoryImpl @Inject constructor(
    private val restClient: RestClient,
    private val dbClient: AppDatabase
    ) : Repository {

    override suspend fun getCitiesByName(cityName: String) = restClient.getCityLatLon(cityName).map { it.toDomain() }

    override suspend fun getWeatherByCoordinates(lat: Double, lon: Double) = restClient.getWeatherForecast(lat, lon).toDomain()

    override suspend fun saveFavouriteCity(city: City) = dbClient.favouriteCityDao().insert(city.toData())

    override fun getFavouriteCities(): Flow<List<City>> = dbClient.favouriteCityDao()
        .getAll()
        .distinctUntilChanged()
        .map { cities ->
            cities.map { city ->
                city.toDomain()
            }
        }

    override suspend fun deleteFavouriteCity(city: City) = dbClient.favouriteCityDao().delete(city.toData())

}

private fun LocationInfoDTO.toDomain() = City(
    name = name,
    state = state,
    country = country,
    latitude = lat,
    longitude = lon
)

private fun ForecastDTO.toDomain() = Weather(
    short = weather[0].main,
    description = weather[0].description,
    iconSmall = "http://openweathermap.org/img/wn/${weather[0].icon}@2x",
    iconLarge = "http://openweathermap.org/img/wn/${weather[0].icon}@4x",
    temperature = main.temp,
    perceivedTemp = main.feelsLike,
    minTemp = main.tempMin,
    maxTemp = main.tempMax,
    pressure = main.pressure,
    humidity = main.humidity,
    visibility = visibility,
    windSpeed = wind.speed,
    windAngle = wind.deg,
    updatedAt = dt,
    cityName = name,
    country = sys.country
)

private fun FavouriteCity.toDomain() = City(uid, name, state, country, latitude, longitude)

private fun City.toData() = FavouriteCity(id, name, state, country, latitude, longitude)