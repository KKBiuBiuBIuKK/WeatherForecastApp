package com.weatherforecast.app.core.common

sealed class AppError(open val message: String) {
    data class Network(override val message: String = "网络连接失败，请检查网络后重试") : AppError(message)
    data class Http(val code: Int, override val message: String = "服务器错误 ($code)") : AppError(message)
    data class Parse(override val message: String = "数据解析失败") : AppError(message)
    data class Unknown(override val message: String = "发生未知错误") : AppError(message)
}

sealed class Outcome<out T> {
    data class Success<T>(val data: T) : Outcome<T>()
    data class Failure(val error: AppError) : Outcome<Nothing>()
}
