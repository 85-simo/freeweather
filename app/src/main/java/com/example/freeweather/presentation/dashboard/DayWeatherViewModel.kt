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

/**
 * Collection of interfaces and classes necessary to implement the main view's presentation and business logic.
 * ViewModels hold the state of the view they refer to, and handle updates in said state through emissions of a specific LiveData stream.
 * Commands are instead to be considered one-shot operations that need to be consumed, and that could potentially be listened to by observers
 * from other views, hence the choice of LiveEvent as the concrete implementation for the command stream. This level of indirection is very useful
 * in ensuring views are as 'dumb' as possible, thus maximizing the unit-testability of business logic.
 * The 'internal' visibility of the implementation is merely symbolical, as this project consists of a single module only.
 * It is however still useful in conveying the necessity not to directly instantiate the class from outside this layer.
 */

private const val TIME_FORMAT = "HH:mm"
private const val DATE_TIME_FORMAT = "EEE, d MMM yyyy, HH:mm"
private const val DATE_FORMAT = "EEE, d MMM"
private const val DEFAULT_LOCATION_NAME = "London, GB"
private const val DEFAULT_LOCATION_LAT = 51.5085
private const val DEFAULT_LOCATION_LON = -0.1257

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
    fun refresh()
}

@HiltViewModel
internal class DayWeatherViewModelImpl @Inject constructor(
    private val repository: Repository
) : DayWeatherViewModel, ViewModel() {
    private lateinit var currentLocation: City

    override val viewStateStream = MutableLiveData<ViewState>()
    override val commands = LiveEvent<Command>()

    init {
        // On init, set the location to the default one.
        locationSet(DEFAULT_LOCATION_NAME, DEFAULT_LOCATION_LAT, DEFAULT_LOCATION_LON)
    }

    // To be invoked every time the user sets their location. It retrieves weather info based on the passed coordinates and
    // notifies observers.
    override fun locationSet(locationCommaSeparated: String, lat: Double, lon: Double) {
        val locationParts = locationCommaSeparated.split(",")
        val locationName = locationParts[0].trim()
        val locationState = if (locationParts.size == 3) locationParts[1].trim() else null
        val locationCountry = if (locationParts.size == 3) locationParts[2].trim() else locationParts[1].trim()
        currentLocation = City(name = locationName, state = locationState, country = locationCountry, latitude = lat, longitude = lon)
        viewModelScope.launch(Dispatchers.IO) {
            var isFavourite = false
            try {
                val weatherForecast = repository.getWeatherByCoordinates(lat, lon)
                isFavourite = repository.getFavouriteCityByCoordinates(lat, lon) != null
                viewStateStream.postValue(ViewState(locationCommaSeparated, isFavourite, weatherForecast.toWeatherInfo())
                )
            } catch (e: Exception) {
                val viewState = viewStateStream.value?.copy(locationName = locationCommaSeparated) ?: ViewState(locationCommaSeparated, isFavourite, emptyList())
                viewStateStream.postValue(viewState)
                commands.postValue(ShowDialog(R.string.generic_error_title, R.string.generic_error_content))
            }
        }
    }

    // To be called every time the user clicks on the search menu action.
    override fun searchClicked() {
        commands.value = Navigate(Destination.LOCATION_SEARCH)
    }

    // Called every time there is a click on the 'set favourite' button.
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

    // Method used to signal that a refresh action has occurred.
    override fun refresh() {
        locationSet(
            listOfNotNull(currentLocation.name, currentLocation.state, currentLocation.country).joinToString(", "),
            currentLocation.latitude,
            currentLocation.longitude
        )
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