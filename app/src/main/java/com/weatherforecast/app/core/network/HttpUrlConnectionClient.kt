package com.weatherforecast.app.core.network

import com.weatherforecast.app.core.common.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

/**
 * Thin wrapper around [HttpURLConnection] — no Retrofit/OkHttp.
 */
class HttpUrlConnectionClient(
    private val connectTimeoutMs: Int = 10_000,
    private val readTimeoutMs: Int = 15_000,
) {

    suspend fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
    ): HttpResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                doInput = true
                setRequestProperty("Accept", "application/json")
                headers.forEach { (key, value) -> setRequestProperty(key, value) }
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }
            val body = stream?.use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { it.readText() }
            }.orEmpty()

            if (code in 200..299) {
                HttpResult.Success(body = body, code = code)
            } else {
                HttpResult.Failure(AppError.Http(code = code))
            }
        } catch (e: SocketTimeoutException) {
            HttpResult.Failure(AppError.Network("请求超时，请稍后重试"))
        } catch (e: UnknownHostException) {
            HttpResult.Failure(AppError.Network())
        } catch (e: IOException) {
            HttpResult.Failure(AppError.Network(e.message ?: "网络连接失败，请检查网络后重试"))
        } catch (e: Exception) {
            HttpResult.Failure(AppError.Unknown(e.message ?: "发生未知错误"))
        } finally {
            connection?.disconnect()
        }
    }
}
