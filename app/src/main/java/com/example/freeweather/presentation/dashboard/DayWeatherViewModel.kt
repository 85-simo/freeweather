package com.example.freeweather.presentation.dashboard

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeweather.R
import com.example.freeweather.data.repository.Repository
import com.example.freeweather.domain.City
import com.example.freeweather.domain.CurrentWeather
import com.example.freeweather.domain.WeatherForecast
import com.example.freeweather.domain.WeatherPrediction
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command.Navigate
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command.Navigate.Destination
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command.ShowDialog
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.ViewState
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
private const val DEFAULT_LOCATION_NAME = "London, GB"
private const val DEFAULT_LOCATION_LAT = 51.509865
private const val DEFAULT_LOCATION_LON = -0.118092

interface DayWeatherViewModel {
    sealed class Command {
        data class ShowDialog(@StringRes val titleResId: Int, @StringRes val contentResId: Int) : Command()
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
        val locationFavourite: Boolean,
        val weatherInfo: List<WeatherInfo>
    )

    val commands: LiveData<Command>
    val viewStateStream: LiveData<ViewState>

    fun locationSet(locationCommaSeparated: String, lat: Double, lon: Double)
    fun searchClicked()
    fun favouriteToggleClicked()
}

@HiltViewModel
internal class DayWeatherViewModelImpl @Inject constructor(
    private val repository: Repository
) : DayWeatherViewModel, ViewModel() {
    private lateinit var currentLocation: City

    override val viewStateStream = MutableLiveData<ViewState>()
    override val commands = LiveEvent<Command>()

    init {
        locationSet(DEFAULT_LOCATION_NAME, DEFAULT_LOCATION_LAT, DEFAULT_LOCATION_LON)
    }

    override fun locationSet(locationCommaSeparated: String, lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherForecast = repository.getWeatherByCoordinates(lat, lon)
                var isFavourite = false
                repository.getFavouriteCityByCoordinates(lat, lon)?.let {
                    isFavourite = true
                    currentLocation = it
                } ?: run {
                    val locationParts = locationCommaSeparated.split(",")
                    val locationName = locationParts[0]
                    val locationState = if (locationParts.size == 3) locationParts[1] else null
                    val locationCountry =
                        if (locationParts.size == 3) locationParts[2] else locationParts[1]
                    currentLocation = City(name = locationName, state = locationState, country = locationCountry, latitude = lat, longitude = lon)
                }
                viewStateStream.postValue(
                    ViewState(locationCommaSeparated, isFavourite, weatherForecast.toWeatherInfo())
                )
            } catch (e: Exception) {
                commands.postValue(ShowDialog(R.string.generic_error_title, R.string.generic_error_content))
            }
        }
    }

    override fun searchClicked() {
        commands.value = Navigate(Destination.LOCATION_SEARCH)
    }

    override fun favouriteToggleClicked() {
        val currentViewState = viewStateStream.value
        val wasFavourite = currentViewState?.locationFavourite ?: false
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (wasFavourite) {
                    repository.deleteFavouriteCity(currentLocation)
                } else {
                    repository.saveFavouriteCity(currentLocation)
                }
                currentViewState?.let {
                    viewStateStream.postValue(currentViewState.copy(locationFavourite = !wasFavourite))
                }
            } catch (e: Exception) {
                commands.postValue(ShowDialog(R.string.generic_error_title, R.string.generic_error_content))
            }
        }
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
    addAll(dailyWeather.drop(0).map { it.toView() })
}