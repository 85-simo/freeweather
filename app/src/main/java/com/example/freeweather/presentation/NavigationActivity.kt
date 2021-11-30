package com.example.freeweather.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.freeweather.databinding.ActivityNavigationBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * This app uses the Single Activity pattern. As such, all user-visible views in the project must be implemented by fragments.
 * This activity should be nothing more than a container of fragments and acts as a navigation controller.
 */

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        installSplashScreen()
        setContentView(binding.root)
    }
}