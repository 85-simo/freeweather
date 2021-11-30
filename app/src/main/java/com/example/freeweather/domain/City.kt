package com.example.freeweather.domain

/**
 * Domain level classes used to provide a data representation that fits the business requirements/logic.
 * By not giving upper layers access to the data-level representation, we minimise the impact that any changes at the data level could
 * have on the rest of the application.
 */

data class City(
    val name: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double
)