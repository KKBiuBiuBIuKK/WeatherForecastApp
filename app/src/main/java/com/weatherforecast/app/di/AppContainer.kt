package com.weatherforecast.app.di

import com.weatherforecast.app.core.network.HttpUrlConnectionClient
import com.weatherforecast.app.data.remote.OpenMeteoApi
import com.weatherforecast.app.data.repository.WeatherRepositoryImpl
import com.weatherforecast.app.domain.repository.WeatherRepository
import com.weatherforecast.app.domain.usecase.GetWeatherForecastUseCase
import com.weatherforecast.app.domain.usecase.SearchLocationsUseCase

/**
 * Manual dependency graph — keeps third-party DI out of scope for this take-home.
 */
class AppContainer {

    private val httpClient: HttpUrlConnectionClient by lazy { HttpUrlConnectionClient() }
    private val openMeteoApi: OpenMeteoApi by lazy { OpenMeteoApi(httpClient) }

    val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(openMeteoApi)
    }

    val searchLocationsUseCase: SearchLocationsUseCase by lazy {
        SearchLocationsUseCase(weatherRepository)
    }

    val getWeatherForecastUseCase: GetWeatherForecastUseCase by lazy {
        GetWeatherForecastUseCase(weatherRepository)
    }
}
