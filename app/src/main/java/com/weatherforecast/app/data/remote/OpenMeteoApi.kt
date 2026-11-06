package com.weatherforecast.app.data.remote

import com.weatherforecast.app.core.common.AppError
import com.weatherforecast.app.core.common.Outcome
import com.weatherforecast.app.core.network.HttpResult
import com.weatherforecast.app.core.network.HttpUrlConnectionClient
import com.weatherforecast.app.domain.model.CurrentWeather
import com.weatherforecast.app.domain.model.DailyForecast
import com.weatherforecast.app.domain.model.Location
import com.weatherforecast.app.domain.model.WeatherForecast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class OpenMeteoApi(
    private val httpClient: HttpUrlConnectionClient,
) {

    suspend fun searchLocations(query: String): Outcome<List<Location>> {
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
        val url =
            "https://geocoding-api.open-meteo.com/v1/search?name=$encoded&count=5&language=zh&format=json"
        return when (val result = httpClient.get(url)) {
            is HttpResult.Failure -> Outcome.Failure(result.error)
            is HttpResult.Success -> parseLocations(result.body)
        }
    }

    suspend fun fetchForecast(location: Location): Outcome<WeatherForecast> {
        val url = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${location.latitude}")
            append("&longitude=${location.longitude}")
            append("&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m")
            append("&daily=weather_code,temperature_2m_max,temperature_2m_min")
            append("&timezone=auto")
            append("&forecast_days=7")
        }
        return when (val result = httpClient.get(url)) {
            is HttpResult.Failure -> Outcome.Failure(result.error)
            is HttpResult.Success -> parseForecast(result.body, location)
        }
    }

    private fun parseLocations(body: String): Outcome<List<Location>> {
        return try {
            val root = JSONObject(body)
            if (!root.has("results")) {
                return Outcome.Success(emptyList())
            }
            val results: JSONArray = root.getJSONArray("results")
            val list = buildList {
                for (i in 0 until results.length()) {
                    val item = results.getJSONObject(i)
                    add(
                        Location(
                            id = item.optLong("id", i.toLong()),
                            name = item.optString("name"),
                            country = item.optString("country"),
                            admin1 = item.optString("admin1").takeIf { it.isNotBlank() },
                            latitude = item.getDouble("latitude"),
                            longitude = item.getDouble("longitude"),
                        )
                    )
                }
            }
            Outcome.Success(list)
        } catch (e: JSONException) {
            Outcome.Failure(AppError.Parse(e.message ?: "地点数据解析失败"))
        }
    }

    private fun parseForecast(body: String, location: Location): Outcome<WeatherForecast> {
        return try {
            val root = JSONObject(body)
            val currentJson = root.getJSONObject("current")
            val current = CurrentWeather(
                temperatureC = currentJson.getDouble("temperature_2m"),
                apparentTemperatureC = currentJson.getDouble("apparent_temperature"),
                humidityPercent = currentJson.getInt("relative_humidity_2m"),
                windSpeedKmh = currentJson.getDouble("wind_speed_10m"),
                weatherCode = currentJson.getInt("weather_code"),
            )

            val dailyJson = root.getJSONObject("daily")
            val dates = dailyJson.getJSONArray("time")
            val codes = dailyJson.getJSONArray("weather_code")
            val maxTemps = dailyJson.getJSONArray("temperature_2m_max")
            val minTemps = dailyJson.getJSONArray("temperature_2m_min")
            val daily = buildList {
                for (i in 0 until dates.length()) {
                    add(
                        DailyForecast(
                            dateIso = dates.getString(i),
                            weatherCode = codes.getInt(i),
                            tempMaxC = maxTemps.getDouble(i),
                            tempMinC = minTemps.getDouble(i),
                        )
                    )
                }
            }

            Outcome.Success(
                WeatherForecast(
                    location = location,
                    current = current,
                    daily = daily,
                )
            )
        } catch (e: JSONException) {
            Outcome.Failure(AppError.Parse(e.message ?: "天气数据解析失败"))
        }
    }
}
