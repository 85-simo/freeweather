package com.example.freeweather.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeweather.data.repository.Repository
import com.example.freeweather.domain.CurrentWeather
import com.example.freeweather.domain.WeatherForecast
import com.example.freeweather.domain.WeatherPrediction
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.*
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command.Navigate
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command.Navigate.Destination.*
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.WeatherInfo.CurrentWeatherInfo
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.WeatherInfo.DailyWeatherInfo
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

private const val TIME_FORMAT = "HH:mm"
private const val DATE_TIME_FORMAT = "EEE, d MMM yyyy HH:mm:ss"
private const val DATE_FORMAT = "EEE, d MMM"

interface DayWeatherViewModel {
    sealed class Command {
        data class Navigate(val destination: Destination) : Command() {
            enum class Destination {
                LOCATION_SEARCH
            }
        }
    }

    sealed class WeatherInfo {
        data class CurrentWeatherInfo(
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
            val dateAndTime: String
        ) : WeatherInfo()

        data class DailyWeatherInfo(
            val date: String,
            val description: String,
            val minTemp: String,
            val maxTemp: String,
            val iconUrl: String
        ) : WeatherInfo()
    }

    data class ViewState(
        val locationName: String,
        val weatherInfo: List<WeatherInfo>
    )

    val commands: LiveData<Command>
    val viewStateStream: LiveData<ViewState>

    fun locationSet(locationName: String, lat: Double, lon: Double)
    fun searchClicked()
}

@HiltViewModel
internal class DayWeatherViewModelImpl @Inject constructor(
    private val repository: Repository
) : DayWeatherViewModel, ViewModel() {

    override val viewStateStream = MutableLiveData<ViewState>()
    override val commands = LiveEvent<Command>()

    override fun locationSet(locationName: String, lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherForecast = repository.getWeatherByCoordinates(lat, lon)
            viewStateStream.postValue(ViewState(locationName, weatherForecast.toWeatherInfo()))
        }
    }

    override fun searchClicked() {
        commands.value = Navigate(LOCATION_SEARCH)
    }
}

private fun CurrentWeather.toView() = CurrentWeatherInfo(
    description = description,
    weatherIconUrl = iconLarge,
    currentTemperature = "${temperature.roundToInt()}° C",
    perceivedTemperature = "${perceivedTemp.roundToInt()}°",
    maxTemp = "${maxTemp.roundToInt()}° C",
    minTemp = "${minTemp.roundToInt()}° C",
    windSpeed = "${windSpeed.roundToInt()} m/s",
    windAngle = "$windAngle°",
    humidityPercent = "$humidity%",
    visibility = "$visibility m",
    pressure = "$pressure hPa",
    sunrise = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(sunrise),
    sunset = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(sunset),
    dateAndTime = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(timestamp)
)

private fun WeatherPrediction.toView() = DailyWeatherInfo(
    date = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(timestamp),
    description = description,
    minTemp = "${minTemp.roundToInt()}°",
    maxTemp = "${maxTemp.roundToInt()}°",
    iconUrl = iconSmall
)

private fun WeatherForecast.toWeatherInfo() = buildList {
    add(currentWeather.toView())
    addAll(dailyWeather.map { it.toView() })
}