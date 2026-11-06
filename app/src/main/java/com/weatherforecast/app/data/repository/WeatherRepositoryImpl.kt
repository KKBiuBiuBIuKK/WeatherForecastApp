package com.weatherforecast.app.data.repository

import com.weatherforecast.app.core.common.Outcome
import com.weatherforecast.app.data.remote.OpenMeteoApi
import com.weatherforecast.app.domain.model.Location
import com.weatherforecast.app.domain.model.WeatherForecast
import com.weatherforecast.app.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: OpenMeteoApi,
) : WeatherRepository {

    override suspend fun searchLocations(query: String): Outcome<List<Location>> {
        return api.searchLocations(query)
    }

    override suspend fun getForecast(location: Location): Outcome<WeatherForecast> {
        return api.fetchForecast(location)
    }
}
