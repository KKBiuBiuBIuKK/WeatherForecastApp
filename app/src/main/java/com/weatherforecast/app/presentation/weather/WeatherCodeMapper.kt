package com.weatherforecast.app.presentation.weather

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Maps WMO weather interpretation codes used by Open-Meteo.
 * https://open-meteo.com/en/docs
 */
object WeatherCodeMapper {

    fun description(code: Int): String = when (code) {
        0 -> "晴朗"
        1 -> "大致晴朗"
        2 -> "局部多云"
        3 -> "阴天"
        45, 48 -> "雾"
        51, 53, 55 -> "毛毛雨"
        56, 57 -> "冻毛毛雨"
        61, 63, 65 -> "降雨"
        66, 67 -> "冻雨"
        71, 73, 75 -> "降雪"
        77 -> "雪粒"
        80, 81, 82 -> "阵雨"
        85, 86 -> "阵雪"
        95 -> "雷暴"
        96, 99 -> "雷暴伴冰雹"
        else -> "未知天气"
    }

    fun icon(code: Int): ImageVector = when (code) {
        0, 1 -> Icons.Outlined.WbSunny
        2 -> Icons.Outlined.WbCloudy
        3, 45, 48 -> Icons.Outlined.Cloud
        51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> Icons.Outlined.WaterDrop
        71, 73, 75, 77, 85, 86 -> Icons.Outlined.AcUnit
        95, 96, 99 -> Icons.Outlined.Thunderstorm
        else -> Icons.Outlined.Cloud
    }
}
