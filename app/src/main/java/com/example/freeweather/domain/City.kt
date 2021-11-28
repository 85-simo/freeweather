package com.example.freeweather.domain

data class City(
    val id: Long = 0,
    val name: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double
)