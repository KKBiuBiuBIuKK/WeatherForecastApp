package com.weatherforecast.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weatherforecast.app.presentation.theme.WeatherTheme
import com.weatherforecast.app.presentation.weather.WeatherScreen
import com.weatherforecast.app.presentation.weather.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as WeatherApp).container

        setContent {
            WeatherTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: WeatherViewModel = viewModel(
                        factory = WeatherViewModel.Factory(
                            searchLocations = container.searchLocationsUseCase,
                            getWeatherForecast = container.getWeatherForecastUseCase,
                        )
                    )
                    WeatherScreen(viewModel = viewModel)
                }
            }
        }
    }
}
