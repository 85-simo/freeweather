package com.example.freeweather.data.api.dto

/**
 * Data Transfer Objects used to provide our JSON deserialiser with a mapping that will also be used by all classes at the data level.
 */

data class LocationInfoDTO(
    val name: String,
    val lat: Double,
    val lon: Double,
    val state: String?,
    val country: String
)