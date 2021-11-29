package com.example.freeweather.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeweather.data.repository.Repository
import com.example.freeweather.domain.WeatherForecast
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.ViewState
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.WeatherInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TIME_FORMAT = "HH:mm"
private const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss.SSS"

interface DayWeatherViewModel {
    data class LocationInfo(
        val locationName: String
    )

    data class WeatherInfo(
        val description: String,
        val weatherIconUrl: String,
        val currentTemperature: String,
        val perceivedTemperature: String,
        val maxTemp: String,
        val minTemp: String,
        val windSpeed: String,
        val windAngle: String,
        val humidityPercent: String,
        val visibility: String,
        val pressure: String,
        val sunrise: String,
        val sunset: String,
        val updatedAt: String
    )

    data class ViewState(
        val weather: WeatherInfo
    )

    val viewStateStream: LiveData<ViewState>

    fun locationSet(lat: Double, lon: Double)
}

@HiltViewModel
internal class DayWeatherViewModelImpl @Inject constructor(
    private val repository: Repository
) : DayWeatherViewModel, ViewModel() {

    override val viewStateStream = MutableLiveData<ViewState>()

    override fun locationSet(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherForecast = repository.getWeatherByCoordinates(lat, lon)
            viewStateStream.postValue(ViewState(weatherForecast.toWeatherInfo()))
        }
    }
}

private fun WeatherForecast.toWeatherInfo() = WeatherInfo(
    description = currentWeather.description,
    weatherIconUrl = currentWeather.iconLarge,
    currentTemperature = "${currentWeather.temperature}° C",
    perceivedTemperature = "${currentWeather.perceivedTemp}°",
    maxTemp = "${currentWeather.maxTemp}° C",
    minTemp = "${currentWeather.minTemp}° C",
    windSpeed = "${currentWeather.windSpeed} m/s",
    windAngle = "${currentWeather.windAngle}°",
    humidityPercent = "${currentWeather.humidity}%",
    visibility = "${currentWeather.visibility} m",
    pressure = "${currentWeather.pressure} hPa",
    sunrise = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(currentWeather.sunrise),
    sunset = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(currentWeather.sunset),
    updatedAt = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(currentWeather.timestamp)
)