package com.example.freeweather.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freeweather.R
import com.example.freeweather.databinding.CurrentWeatherItemBinding
import com.example.freeweather.databinding.DailyWeatherItemBinding
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.WeatherInfo
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.WeatherInfo.CurrentWeatherInfo
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.WeatherInfo.DailyWeatherInfo
import com.squareup.picasso.Picasso

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class WeatherAdapter(private var dailyWeatherInfoList: List<WeatherInfo>, private val favouriteToggleClickListener: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isFavouriteLocation = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> CurrentWeatherViewHolder(CurrentWeatherItemBinding.inflate(inflater, parent, false))
            ITEM_VIEW_TYPE_ITEM -> DailyWeatherViewHolder(DailyWeatherItemBinding.inflate(inflater, parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = dailyWeatherInfoList[position]
        when (holder) {
            is CurrentWeatherViewHolder -> holder.bind(currentItem as CurrentWeatherInfo, isFavouriteLocation, favouriteToggleClickListener)
            is DailyWeatherViewHolder -> holder.bind(currentItem as DailyWeatherInfo)
        }
    }

    override fun getItemCount() = dailyWeatherInfoList.size

    override fun getItemViewType(position: Int): Int {
        return when(dailyWeatherInfoList[position]) {
            is CurrentWeatherInfo -> ITEM_VIEW_TYPE_HEADER
            is DailyWeatherInfo -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun submitList(dailyWeatherInfoList: List<WeatherInfo>) {
        this.dailyWeatherInfoList = dailyWeatherInfoList
        notifyDataSetChanged()
    }

    fun setFavouriteLocation(favourite: Boolean) {
        isFavouriteLocation = favourite
        notifyItemChanged(0)
    }
}

private class CurrentWeatherViewHolder(private val binding: CurrentWeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(weatherInfo: CurrentWeatherInfo, isFavouriteLocation: Boolean, favouriteToggleClickListener: () -> Unit) {
        with(binding) {
            datetime.text = weatherInfo.dateAndTime
            currentWeatherDesc.text = weatherInfo.description
            maxTempValue.text = weatherInfo.maxTemp
            minTempValue.text = weatherInfo.minTemp
            feelsLikeValue.text = weatherInfo.perceivedTemperature
            temperature.text = weatherInfo.currentTemperature
            humidityValue.text = weatherInfo.humidityPercent
            visibilityValue.text = weatherInfo.visibility
            pressureValue.text = weatherInfo.pressure
            windSpeedValue.text = weatherInfo.windSpeed
            windAngleValue.text = weatherInfo.windAngle
            sunriseValue.text = weatherInfo.sunrise
            sunsetValue.text = weatherInfo.sunset
            Picasso.get()
                .load(weatherInfo.weatherIconUrl)
                .resizeDimen(R.dimen.weather_icon_size, R.dimen.weather_icon_size)
                .into(currentWeatherIcon)
            val favouriteImageRes = if (isFavouriteLocation) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
            favouriteButton.setImageResource(favouriteImageRes)
            favouriteButton.setOnClickListener {
                favouriteToggleClickListener.invoke()
            }
        }
    }
}

private class DailyWeatherViewHolder(private val binding: DailyWeatherItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(weatherInfo: DailyWeatherInfo) {
        with(binding) {
            date.text = weatherInfo.date
            description.text = weatherInfo.description
            minTemp.text = weatherInfo.minTemp
            maxTemp.text = weatherInfo.maxTemp
            Picasso.get()
                .load(weatherInfo.iconUrl)
                .into(weatherIcon)
        }
    }
}