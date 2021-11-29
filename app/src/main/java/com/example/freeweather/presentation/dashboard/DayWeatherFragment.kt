package com.example.freeweather.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.freeweather.R
import com.example.freeweather.databinding.FragmentDayWeatherBinding
import com.example.freeweather.presentation.BaseFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayWeatherFragment : BaseFragment<FragmentDayWeatherBinding>() {
    private val dayWeatherViewModel: DayWeatherViewModel by viewModels<DayWeatherViewModelImpl>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDayWeatherBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayWeatherViewModel.viewStateStream.observe(viewLifecycleOwner) { viewState ->
            with(binding) {
                datetime.text = viewState.weather.updatedAt
                currentWeatherDesc.text = viewState.weather.description
                maxTempValue.text = viewState.weather.maxTemp
                minTempValue.text = viewState.weather.minTemp
                feelsLikeValue.text = viewState.weather.perceivedTemperature
                temperature.text = viewState.weather.currentTemperature
                humidityValue.text = viewState.weather.humidityPercent
                visibilityValue.text = viewState.weather.visibility
                pressureValue.text = viewState.weather.pressure
                windSpeedValue.text = viewState.weather.windSpeed
                windAngleValue.text = viewState.weather.windAngle
                sunriseValue.text = viewState.weather.sunrise
                sunsetValue.text = viewState.weather.sunset
                Picasso.get()
                    .load(viewState.weather.weatherIconUrl)
                    .resizeDimen(R.dimen.weather_icon_size, R.dimen.weather_icon_size)
                    .into(currentWeatherIcon)
            }
        }
        dayWeatherViewModel.locationSet( 51.509865, -0.118092)
    }
}