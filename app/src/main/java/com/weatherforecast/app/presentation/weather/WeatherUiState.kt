package com.weatherforecast.app.presentation.weather

import com.weatherforecast.app.domain.model.CurrentWeather
import com.weatherforecast.app.domain.model.DailyForecast
import com.weatherforecast.app.domain.model.Location

data class WeatherUiState(
    val query: String = "",
    val searchResults: List<Location> = emptyList(),
    val isSearching: Boolean = false,
    val weatherLoading: Boolean = false,
    val selectedLocation: Location? = null,
    val locationName: String? = null,
    val current: CurrentWeather? = null,
    val daily: List<DailyForecast> = emptyList(),
    val errorMessage: String? = null,
    val searchErrorMessage: String? = null,
)
