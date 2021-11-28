package com.example.freeweather.data.api

import android.content.Context
import com.example.freeweather.data.api.dto.*
import com.example.freeweather.hilt.modules.ApiModule
import com.example.freeweather.utils.enqueueResponse
import kotlinx.coroutines.runBlocking
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
        val interceptor = ApiModule.provideHttpLoggingInterceptor()
        val cache = ApiModule.provideApiCache(mockContext)
        val client = ApiModule.provideHttpClient(interceptor, cache)
            .newBuilder()
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
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
            val expected = ForecastDTO(
                CoordDTO( 37.39, -122.08),
                listOf(WeatherDTO(800, "Clear", "clear sky", "01d")),
                MainDTO(282.55, 281.86, 280.37, 284.26, 1023.0, 100.0),
                16093.0,
                WindDTO(1.5, 350.0),
                CloudsDTO(1.0),
                rain = null,
                snow = null,
                1560350645L,
                SysDTO("US", 1560343627L, 1560396563L),
                -25200L,
                420006353L,
                "Mountain View"
            )
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
}