package com.example.freeweather.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freeweather.NavGraphDirections
import com.example.freeweather.R
import com.example.freeweather.databinding.FragmentDayWeatherBinding
import com.example.freeweather.presentation.BaseFragment
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command.Navigate
import com.example.freeweather.presentation.dashboard.DayWeatherViewModel.Command.Navigate.Destination.LOCATION_SEARCH
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayWeatherFragment : BaseFragment<FragmentDayWeatherBinding>() {
    private val dayWeatherViewModel: DayWeatherViewModel by activityViewModels<DayWeatherViewModelImpl>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDayWeatherBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val defaultItemAnimator = DefaultItemAnimator()
        val weatherAdapter = WeatherAdapter(emptyList()) {
            dayWeatherViewModel.favouriteToggleClicked()
        }
        val listDivider = DividerItemDecoration(requireActivity(), LinearLayout.HORIZONTAL)
        with(binding.weatherRecycler) {
            layoutManager = linearLayoutManager
            itemAnimator = defaultItemAnimator
            addItemDecoration(listDivider)
            adapter = weatherAdapter
        }
        binding.titlebar.inflateMenu(R.menu.menu)
        binding.titlebar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_search -> dayWeatherViewModel.searchClicked()
                else -> Unit // do nothing
            }
            true
        }
        binding.swipeRefresh.setOnRefreshListener {
            dayWeatherViewModel.refresh()
        }

        dayWeatherViewModel.viewStateStream.observe(viewLifecycleOwner) { viewState ->
            binding.titlebar.title = viewState.locationName
            weatherAdapter.submitList(viewState.weatherInfo)
            weatherAdapter.setFavouriteLocation(viewState.locationFavourite)
            if (binding.swipeRefresh.isRefreshing) {
                binding.swipeRefresh.isRefreshing = false
            }
        }
        dayWeatherViewModel.commands.observe(viewLifecycleOwner) { command ->
            when (command) {
                is Navigate -> {
                    when (command.destination) {
                        LOCATION_SEARCH -> findNavController().navigate(R.id.action_dayWeatherFragment_to_searchFragment)
                    }
                }
                is DayWeatherViewModel.Command.ShowDialog -> {
                    val action = NavGraphDirections.actionGlobalSimpleDialogFragment(command.titleResId, command.contentResId)
                    findNavController().navigate(action)
                }
            }
        }
    }
}