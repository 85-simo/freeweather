package com.example.freeweather.data.repository

import com.example.freeweather.data.api.RestClient
import com.example.freeweather.data.api.dto.*
import com.example.freeweather.data.db.AppDatabase
import com.example.freeweather.data.db.entities.FavouriteCity
import com.example.freeweather.data.db.entities.FavouriteCityDao
import com.example.freeweather.domain.City
import com.example.freeweather.domain.CurrentWeather
import com.example.freeweather.domain.WeatherForecast
import com.example.freeweather.domain.WeatherPrediction
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.util.*
import java.util.concurrent.TimeUnit

private const val LOCATION_NAME = "Test"
private const val LOCATION_LATITUDE = 12.6
private const val LOCATION_LONGITUDE = 32.4
private const val LOCATION_STATE = "aState"
private const val LOCATION_COUNTRY = "aCountry"
private const val WEATHER_ID = 12L
private const val WEATHER_MAIN = "aMain"
private const val WEATHER_DESC = "aDesc"
private const val WEATHER_ICON = "anIcon"
private const val TEMPERATURE = 32.0F
private const val PRESSURE = 1012
private const val HUMIDITY = 17
private const val VISIBILITY = 1762
private const val WIND_SPEED = 17.0F
private const val WIND_ANGLE = 12
private const val CLOUDS = 22
private const val RAIN_LAST_HR = 1.7F
private const val SNOW_LAST_HR = 0.2F
private const val UPDATED_AT = 2463231L
private const val SUNRISE_AT = 1451552L
private const val SUNSET_AT = 2564572L
private const val TIMEZONE = "Europe/London"
private const val TIMEZONE_OFFSET = 431144L
private const val ICON_API_BASE = "https://openweathermap.org/img/wn/"
private const val ICON_URL_SMALL_SUFFIX = "@2x.png"
private const val ICON_URL_LARGE_SUFFIX = "@4x.png"
private const val DEW_POINT = 2.4F
private const val UVI = 1.2F
private const val MOON_PHASE = 0.45F
private const val POP = 0.12F

class RepositoryTest {
    private lateinit var repository: Repository

    private val mockRestClient: RestClient = mock()
    private val mockDbClient: AppDatabase = mock()
    private val mockCityDao: FavouriteCityDao = mock()

    @Before
    fun setUp() {
        mockRestClient.stub {
            onBlocking { getCityLatLon(any()) } doReturn listOf(getLocationInfo())
            onBlocking { getWeatherForecast(any(), any()) } doReturn getWeatherForecast()
        }
        mockCityDao.stub {
            onBlocking { getAll() } doReturn flowOf(getFavouriteCities())
        }
        whenever(mockDbClient.favouriteCityDao()).thenReturn(mockCityDao)
        repository = RepositoryImpl(mockRestClient, mockDbClient)
    }

    @Test
    fun testGetCitiesByName() {
        runBlocking {
            val expected = listOf(City(
                LOCATION_NAME,
                LOCATION_STATE,
                LOCATION_COUNTRY,
                LOCATION_LATITUDE,
                LOCATION_LONGITUDE
            ))
            val actual = repository.getCitiesByName(LOCATION_NAME)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun testGetWeatherByCoords() {
        val expected = getExpectedWeatherForecast()
        runBlocking {
            val actual = repository.getWeatherByCoordinates(LOCATION_LATITUDE, LOCATION_LONGITUDE)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun testSaveFavouriteCity() {
        val expected = getFavouriteCity()
        val captor = argumentCaptor<FavouriteCity>()
        runBlocking {
            repository.saveFavouriteCity(getExpectedCity())
            verify(mockCityDao).insert(captor.capture())
            assertEquals(expected, captor.lastValue)
        }
    }

    @Test
    fun testDeleteFavouriteCity() {
        val expected = getFavouriteCity()
        val captor = argumentCaptor<FavouriteCity>()
        runBlocking {
            repository.deleteFavouriteCity(getExpectedCity())
            verify(mockCityDao).delete(captor.capture())
            assertEquals(expected, captor.lastValue)
        }
    }

    @Test
    fun testGetFavouriteCities() {
        val expected = listOf(getExpectedCities())
        runBlocking {
            val actual = repository.getFavouriteCities()
                .toList()
            assertEquals(expected, actual)
        }
    }

    private fun getLocationInfo() = LocationInfoDTO(
        LOCATION_NAME,
        LOCATION_LATITUDE,
        LOCATION_LONGITUDE,
        LOCATION_STATE,
        LOCATION_COUNTRY
    )

    private fun getWeatherForecast() = ForecastDTO(
        LOCATION_LATITUDE,
        LOCATION_LONGITUDE,
        TIMEZONE,
        TIMEZONE_OFFSET,
        CurrentDTO(
            UPDATED_AT,
            SUNRISE_AT,
            SUNSET_AT,
            TEMPERATURE,
            TEMPERATURE,
            PRESSURE,
            HUMIDITY,
            DEW_POINT,
            UVI,
            CLOUDS,
            VISIBILITY,
            WIND_SPEED,
            WIND_ANGLE,
            null,
            listOf(WeatherDTO(WEATHER_ID, WEATHER_MAIN, WEATHER_DESC, WEATHER_ICON)),
            RainDTO(RAIN_LAST_HR),
            SnowDTO(SNOW_LAST_HR)
        ),
        listOf(
            DailyDTO(
                UPDATED_AT,
                SUNRISE_AT,
                SUNSET_AT,
                SUNRISE_AT,
                SUNSET_AT,
                MOON_PHASE,
                TempDTO(TEMPERATURE, TEMPERATURE, TEMPERATURE, TEMPERATURE, TEMPERATURE, TEMPERATURE),
                FeelsLikeDTO(TEMPERATURE, TEMPERATURE, TEMPERATURE, TEMPERATURE),
                PRESSURE,
                HUMIDITY,
                DEW_POINT,
                WIND_SPEED,
                WIND_ANGLE,
                null,
                listOf(WeatherDTO(WEATHER_ID, WEATHER_MAIN, WEATHER_DESC, WEATHER_ICON)),
                CLOUDS,
                POP,
                RAIN_LAST_HR,
                UVI,
                SNOW_LAST_HR
            )
        )
    )

    private fun getExpectedWeatherForecast() = WeatherForecast(
        CurrentWeather(
            WEATHER_DESC,
            "$ICON_API_BASE$WEATHER_ICON$ICON_URL_SMALL_SUFFIX",
            "$ICON_API_BASE$WEATHER_ICON$ICON_URL_LARGE_SUFFIX",
            TEMPERATURE,
            TEMPERATURE,
            TEMPERATURE,
            TEMPERATURE,
            PRESSURE,
            HUMIDITY,
            VISIBILITY,
            WIND_SPEED,
            WIND_ANGLE,
            Date(TimeUnit.SECONDS.toMillis(UPDATED_AT)),
            Date(TimeUnit.SECONDS.toMillis(SUNRISE_AT)),
            Date(TimeUnit.SECONDS.toMillis(SUNSET_AT)),
        ),
        listOf(
            WeatherPrediction(
                WEATHER_DESC,
                "$ICON_API_BASE$WEATHER_ICON$ICON_URL_SMALL_SUFFIX",
                "$ICON_API_BASE$WEATHER_ICON$ICON_URL_LARGE_SUFFIX",
                TEMPERATURE,
                TEMPERATURE,
                Date(TimeUnit.SECONDS.toMillis(UPDATED_AT))
            )
        )
    )

    private fun getFavouriteCity() = FavouriteCity(
        LOCATION_NAME,
        LOCATION_STATE,
        LOCATION_COUNTRY,
        LOCATION_LATITUDE,
        LOCATION_LONGITUDE
    )

    private fun getFavouriteCities() = listOf(
        getFavouriteCity(),
        getFavouriteCity()
    )

    private fun getExpectedCity() = City(
        LOCATION_NAME,
        LOCATION_STATE,
        LOCATION_COUNTRY,
        LOCATION_LATITUDE,
        LOCATION_LONGITUDE
    )

    private fun getExpectedCities() = listOf(
        getExpectedCity(),
        getExpectedCity()
    )
}