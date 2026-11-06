package com.weatherforecast.app.domain.usecase

import com.weatherforecast.app.core.common.Outcome
import com.weatherforecast.app.domain.model.Location
import com.weatherforecast.app.domain.model.WeatherForecast
import com.weatherforecast.app.domain.repository.WeatherRepository

class GetWeatherForecastUseCase(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(location: Location): Outcome<WeatherForecast> {
        return repository.getForecast(location)
    }
}
