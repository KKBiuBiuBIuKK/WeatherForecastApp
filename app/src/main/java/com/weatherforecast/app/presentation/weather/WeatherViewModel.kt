package com.weatherforecast.app.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weatherforecast.app.core.common.Outcome
import com.weatherforecast.app.domain.model.Location
import com.weatherforecast.app.domain.usecase.GetWeatherForecastUseCase
import com.weatherforecast.app.domain.usecase.SearchLocationsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val searchLocations: SearchLocationsUseCase,
    private val getWeatherForecast: GetWeatherForecastUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var weatherJob: Job? = null

    init {
        // Default city so the first screen is not empty.
        val default = Location(
            id = 1_668_348,
            name = "北京",
            country = "中国",
            admin1 = "北京市",
            latitude = 39.9075,
            longitude = 116.3972,
        )
        _uiState.update { it.copy(query = default.name, selectedLocation = default) }
        loadForecast(default)
    }

    fun onQueryChange(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                searchErrorMessage = null,
                errorMessage = null,
            )
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            performSearch(query)
        }
    }

    fun onLocationSelected(location: Location) {
        searchJob?.cancel()
        _uiState.update {
            it.copy(
                query = location.name,
                selectedLocation = location,
                searchResults = emptyList(),
                searchErrorMessage = null,
                errorMessage = null,
            )
        }
        loadForecast(location)
    }

    fun retry() {
        val location = _uiState.value.selectedLocation
        if (location != null) {
            loadForecast(location)
        } else {
            performSearch(_uiState.value.query)
        }
    }

    fun clearSearchResults() {
        _uiState.update { it.copy(searchResults = emptyList()) }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchErrorMessage = null) }
            when (val outcome = searchLocations(query)) {
                is Outcome.Success -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResults = outcome.data,
                            searchErrorMessage = if (query.trim().length >= 2 && outcome.data.isEmpty()) {
                                "未找到匹配地点"
                            } else {
                                null
                            },
                        )
                    }
                }
                is Outcome.Failure -> {
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResults = emptyList(),
                            searchErrorMessage = outcome.error.message,
                        )
                    }
                }
            }
        }
    }

    private fun loadForecast(location: Location) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    weatherLoading = true,
                    errorMessage = null,
                    locationName = location.displayName,
                )
            }
            when (val outcome = getWeatherForecast(location)) {
                is Outcome.Success -> {
                    _uiState.update {
                        it.copy(
                            weatherLoading = false,
                            locationName = outcome.data.location.displayName,
                            current = outcome.data.current,
                            daily = outcome.data.daily,
                            errorMessage = null,
                        )
                    }
                }
                is Outcome.Failure -> {
                    _uiState.update {
                        it.copy(
                            weatherLoading = false,
                            errorMessage = outcome.error.message,
                        )
                    }
                }
            }
        }
    }

    class Factory(
        private val searchLocations: SearchLocationsUseCase,
        private val getWeatherForecast: GetWeatherForecastUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(WeatherViewModel::class.java))
            return WeatherViewModel(searchLocations, getWeatherForecast) as T
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}
