package com.japanesehelper.data.remote.dto

data class TemperatureDescriptionResponseDto(
    val sentence: String,
    val translation: String,
    val temperature: Double
)
