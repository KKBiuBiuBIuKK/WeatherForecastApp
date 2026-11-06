package com.weatherforecast.app.presentation.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherforecast.app.presentation.components.CurrentWeatherCard
import com.weatherforecast.app.presentation.components.DailyForecastList
import com.weatherforecast.app.presentation.components.ErrorContent
import com.weatherforecast.app.presentation.components.LoadingContent
import com.weatherforecast.app.presentation.components.LocationSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "天气预报",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            LocationSearchBar(
                query = state.query,
                results = state.searchResults,
                isSearching = state.isSearching,
                searchErrorMessage = state.searchErrorMessage,
                onQueryChange = viewModel::onQueryChange,
                onLocationSelected = viewModel::onLocationSelected,
                onClearResults = viewModel::clearSearchResults,
            )

            Spacer(modifier = Modifier.height(16.dp))

            val current = state.current
            when {
                state.weatherLoading && current == null -> {
                    LoadingContent()
                }
                state.errorMessage != null && current == null -> {
                    ErrorContent(
                        message = state.errorMessage.orEmpty(),
                        onRetry = viewModel::retry,
                    )
                }
                current != null -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            if (state.weatherLoading) {
                                LoadingContent(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                            }
                            if (state.errorMessage != null) {
                                ErrorContent(
                                    message = state.errorMessage.orEmpty(),
                                    onRetry = viewModel::retry,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                            }
                            CurrentWeatherCard(
                                locationName = state.locationName.orEmpty(),
                                current = current,
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            if (state.daily.isNotEmpty()) {
                                DailyForecastList(daily = state.daily)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}
