package com.japanesehelper.domain.model

/**
 * Result of running the Temperature Description prompt at one sampling
 * temperature: the same prompt, only [temperature] differs between requests.
 *
 * @param sentence The generated Japanese sentence.
 * @param translation Its Russian translation.
 * @param temperature The sampling temperature that produced this result.
 */
data class TemperatureDescription(
    val sentence: String,
    val translation: String,
    val temperature: Double
)

/** The three temperatures this experiment compares - must match the backend. */
val SUPPORTED_TEMPERATURES: List<Double> = listOf(0.0, 0.7, 1.2)
