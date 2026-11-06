package com.weatherforecast.app

import android.app.Application
import com.weatherforecast.app.di.AppContainer

class WeatherApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
