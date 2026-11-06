package com.weatherforecast.app.domain.model

data class Location(
    val id: Long,
    val name: String,
    val country: String,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double,
) {
    val displayName: String
        get() = buildString {
            append(name)
            if (!admin1.isNullOrBlank()) append(" · ").append(admin1)
            if (country.isNotBlank()) append(" · ").append(country)
        }
}
