package com.weatherforecast.app.domain.usecase

import com.weatherforecast.app.core.common.Outcome
import com.weatherforecast.app.domain.model.Location
import com.weatherforecast.app.domain.repository.WeatherRepository

class SearchLocationsUseCase(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(query: String): Outcome<List<Location>> {
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            return Outcome.Success(emptyList())
        }
        return repository.searchLocations(trimmed)
    }
}
