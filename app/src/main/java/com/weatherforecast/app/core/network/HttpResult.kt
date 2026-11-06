package com.weatherforecast.app.core.network

import com.weatherforecast.app.core.common.AppError

sealed class HttpResult {
    data class Success(val body: String, val code: Int) : HttpResult()
    data class Failure(val error: AppError) : HttpResult()
}
