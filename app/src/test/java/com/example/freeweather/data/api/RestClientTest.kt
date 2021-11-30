package com.example.freeweather.data.api

import android.content.Context
import com.example.freeweather.data.api.dto.*
import com.example.freeweather.hilt.modules.ApiModule
import com.example.freeweather.utils.enqueueResponse
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val WEATHER_SUCCESS_RESPONSE = "get-weather-forecast-200.json"
private const val CITY_TO_LATLON_SUCCESS_RESPONSE = "get-latlon-by-city-name-200.json"
private const val API_KEY_QUERY_PARAM = "appid"

class RestClientTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val mockWebServer = MockWebServer()
    private val mockContext: Context = mock()

    private lateinit var restClient: RestClient

    @Before
    fun setUp() {
        whenever(mockContext.cacheDir).thenReturn(tempFolder.root)
        val apiKeyInterceptor = ApiModule.provideApiKeyInterceptor()
        val cache = ApiModule.provideApiCache(mockContext)
        val client = OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .addInterceptor(apiKeyInterceptor)
            .cache(cache)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val weatherClient = retrofit.create(RestWeather::class.java)
        val geocodingClient = retrofit.create(RestGeocoding::class.java)
        restClient = RestClientImpl(weatherClient, geocodingClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetWeatherForecastApiSuccess() {
        mockWebServer.enqueueResponse(WEATHER_SUCCESS_RESPONSE, 200)
        runBlocking {
            val actual = restClient.getWeatherForecast(37.39, -122.08)
            val expected = getExpectedForecastDTO()
            assertEquals(expected, actual)
        }
    }

    @Test
    fun testGetLatLonByCityNameSuccess() {
        mockWebServer.enqueueResponse(CITY_TO_LATLON_SUCCESS_RESPONSE, 200)
        runBlocking {
            val actual = restClient.getCityLatLon("London")
            val expected = listOf(
                LocationInfoDTO("London", 51.5085, -0.1257, state = null, "GB"),
                LocationInfoDTO("London", 42.9834, -81.233, state = null, "CA"),
                LocationInfoDTO("London", 39.8865, -83.4483, "OH", "US"),
                LocationInfoDTO("London", 37.129, -84.0833, "KY", "US"),
                LocationInfoDTO("London", 36.4761, -119.4432, "CA", "US")
            )
            assertEquals(expected, actual)
        }
    }

    @Test
    fun testApiKeyPresentInWeatherRequest() {
        mockWebServer.enqueueResponse(WEATHER_SUCCESS_RESPONSE, 200)
        runBlocking {
            restClient.getWeatherForecast(0.0, 0.0)
        }
        val request = mockWebServer.takeRequest()
        val apiKey = request.requestUrl?.queryParameter(API_KEY_QUERY_PARAM)
        assertNotNull(apiKey)
    }

    @Test
    fun testApiKeyPresentInGeocodingRequest() {
        mockWebServer.enqueueResponse(CITY_TO_LATLON_SUCCESS_RESPONSE, 200)
        runBlocking {
            restClient.getCityLatLon("London")
        }
        val request = mockWebServer.takeRequest()
        val apiKey = request.requestUrl?.queryParameter(API_KEY_QUERY_PARAM)
        assertNotNull(apiKey)
    }

    private fun getExpectedForecastDTO() = ForecastDTO(
        lat = 51.5099,
        lon = -0.1181,
        timezone = "Europe/London",
        timezoneOffsetSeconds = 0,
        current = CurrentDTO(
            dtSeconds = 1638125904,
            sunriseSeconds = 1638085190,
            sunsetSeconds = 1638115049,
            temp = 2.6F,
            feelsLike = -0.82F,
            pressure = 1007,
            humidity = 65,
            dewPoint = -2.93F,
            uvi = 0F,
            clouds = 100,
            visibility = 10_000,
            windSpeed = 3.6F,
            windDeg = 300,
            weather = listOf(WeatherDTO(
                id = 804L,
                main = "Clouds",
                description = "overcast clouds",
                icon = "04n"
            )),
            rain = null,
            snow = null,
            windGust = null
        ),
        daily = listOf(
            DailyDTO(
                dtSeconds = 1638097200L,
                sunriseSeconds = 1638085190L,
                sunsetSeconds = 1638115049L,
                moonriseSeconds = 0L,
                moonsetSeconds = 1638106980L,
                moonPhase = 0.78F,
                TempDTO(
                    day = 2.92F,
                    min = 0.81F,
                    max = 4.32F,
                    night = 1.61F,
                    eve = 2.89F,
                    morn = 1.41F
                ),
                FeelsLikeDTO(
                    day = -1.33F,
                    night = -1.84F,
                    eve = -0.13F,
                    morn = -3.89F
                ),
                pressure = 1006,
                humidity = 56,
                dewPoint = -5.14F,
                windSpeed = 7.64F,
                windDeg = 334,
                windGust = 15.7F,
                weather = listOf(WeatherDTO(
                    id = 803L,
                    main = "Clouds",
                    description = "broken clouds",
                    icon = "04d"
                )),
                clouds = 65,
                pop = 0.13F,
                uvi = 0.58F,
                rain = null,
                snow = null
            )
        )
    )
}