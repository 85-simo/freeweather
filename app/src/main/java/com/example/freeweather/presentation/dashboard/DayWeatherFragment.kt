package com.example.freeweather.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freeweather.R
import com.example.freeweather.databinding.FragmentDayWeatherBinding
import com.example.freeweather.presentation.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

private const val DEFAULT_LOCATION_NAME = "London, GB"
private const val DEFAULT_LOCATION_LAT = 51.509865
private const val DEFAULT_LOCATION_LON = -0.118092

@AndroidEntryPoint
class DayWeatherFragment : BaseFragment<FragmentDayWeatherBinding>() {
    private val dayWeatherViewModel: DayWeatherViewModel by viewModels<DayWeatherViewModelImpl>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDayWeatherBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val defaultItemAnimator = DefaultItemAnimator()
        val weatherAdapter = WeatherAdapter(emptyList())
        val listDivider = DividerItemDecoration(requireActivity(), LinearLayout.HORIZONTAL)
        with(binding.weatherRecycler) {
            layoutManager = linearLayoutManager
            itemAnimator = defaultItemAnimator
            addItemDecoration(listDivider)
            adapter = weatherAdapter
        }
        binding.titlebar.inflateMenu(R.menu.menu)
        dayWeatherViewModel.viewStateStream.observe(viewLifecycleOwner) { viewState ->
            binding.titlebar.title = viewState.locationName
            weatherAdapter.submitList(viewState.weatherInfo)
        }
        dayWeatherViewModel.locationSet( DEFAULT_LOCATION_NAME, DEFAULT_LOCATION_LAT, DEFAULT_LOCATION_LON)
    }
}