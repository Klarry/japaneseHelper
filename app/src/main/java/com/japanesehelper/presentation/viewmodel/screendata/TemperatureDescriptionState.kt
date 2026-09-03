package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.SUPPORTED_TEMPERATURES
import com.japanesehelper.domain.model.TemperatureDescription

/**
 * Independent state of one temperature's experiment section.
 *
 * [Idle] is the initial state for every temperature - nothing is requested
 * until the user taps that section's "Run experiment" button, unlike the
 * Kanji Word Set tabs which load themselves on open.
 */
sealed class TemperatureResultUiState {
    data object Idle : TemperatureResultUiState()
    data object Loading : TemperatureResultUiState()
    data class Success(val result: TemperatureDescription) : TemperatureResultUiState()
    data class Error(val message: String) : TemperatureResultUiState()
}

/**
 * UI state for the Temperature Description screen.
 *
 * @param kanji The dynamic word this screen instance was opened for - the
 * whole word is sent to the backend, matching Kanji Word Set/AI Explanation.
 * @param furigana The word's furigana, shown as basic info under the title.
 * @param meaning The word's meaning, shown as basic info under the title.
 * @param results Per-temperature state, keyed by [SUPPORTED_TEMPERATURES].
 */
data class TemperatureDescriptionScreenState(
    val kanji: String,
    val furigana: String = "",
    val meaning: String = "",
    val results: Map<Double, TemperatureResultUiState> =
        SUPPORTED_TEMPERATURES.associateWith { TemperatureResultUiState.Idle }
)
