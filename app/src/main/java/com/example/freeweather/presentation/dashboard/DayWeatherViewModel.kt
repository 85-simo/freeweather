package com.example.freeweather.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freeweather.data.repository.Repository
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface DayWeatherViewModel {
    sealed class Command {
        data class Navigate(val destination: Destination) : Command() {
            enum class Destination {
                LOCATION_SEARCH
            }
        }
    }

    data class ViewState(
        val weatherDescShort: String,
        val weatherDescLong: String,
        val weatherIconUrl: String,
        val currentTemperature: String,
        val perceivedTemperature: String,
        val windSpeed: String,
        val windAngle: String,
        val humidityPercent: String,
        val visibility: String,
        val pressure: String,
        val sunrise: String,
        val sunset: String,
        val updatedAt: String
    )

    val commands: LiveData<Command>
    val viewState: LiveData<ViewState>

    fun searchButtonClicked()
    fun locationSet(name: String, lat: Double, lon: Double)
}

@HiltViewModel
internal class DayWeatherViewModelImpl @Inject constructor(
    private val repository: Repository
) : DayWeatherViewModel, ViewModel() {

    override val commands = LiveEvent<Command>()
    override val viewState = MutableLiveData<DayWeatherViewModel.ViewState>()

    override fun searchButtonClicked() {

    }

    override fun locationSet(name: String, lat: Double, lon: Double) {
        TODO("Not yet implemented")
    }

}