package com.weatherforecast.app.domain.model

data class CurrentWeather(
    val temperatureC: Double,
    val apparentTemperatureC: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val weatherCode: Int,
)

data class DailyForecast(
    val dateIso: String,
    val weatherCode: Int,
    val tempMaxC: Double,
    val tempMinC: Double,
)

data class WeatherForecast(
    val location: Location,
    val current: CurrentWeather,
    val daily: List<DailyForecast>,
)
