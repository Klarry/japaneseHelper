package com.japanesehelper.domain.model

data class TemperatureDescription(
    val sentence: String,
    val translation: String,
    val temperature: Double
)

/** Must match the values the backend accepts. */
val SUPPORTED_TEMPERATURES: List<Double> = listOf(0.0, 0.7, 1.2)
