package com.example.freeweather.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.freeweather.databinding.FragmentDayWeatherBinding
import com.example.freeweather.presentation.BaseFragment

class DayWeatherFragment : BaseFragment<FragmentDayWeatherBinding>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDayWeatherBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}