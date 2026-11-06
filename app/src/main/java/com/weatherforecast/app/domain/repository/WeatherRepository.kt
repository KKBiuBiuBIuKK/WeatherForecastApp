package com.weatherforecast.app.domain.repository

import com.weatherforecast.app.core.common.Outcome
import com.weatherforecast.app.domain.model.Location
import com.weatherforecast.app.domain.model.WeatherForecast

interface WeatherRepository {
    suspend fun searchLocations(query: String): Outcome<List<Location>>
    suspend fun getForecast(location: Location): Outcome<WeatherForecast>
}
