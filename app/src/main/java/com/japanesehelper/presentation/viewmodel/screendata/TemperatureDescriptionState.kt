package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.SUPPORTED_TEMPERATURES
import com.japanesehelper.domain.model.TemperatureDescription

sealed class TemperatureResultUiState {
    data object Idle : TemperatureResultUiState()
    data object Loading : TemperatureResultUiState()
    data class Success(val result: TemperatureDescription) : TemperatureResultUiState()
    data class Error(val message: String) : TemperatureResultUiState()
}

data class TemperatureDescriptionScreenState(
    val kanji: String,
    val furigana: String = "",
    val meaning: String = "",
    val results: Map<Double, TemperatureResultUiState> =
        SUPPORTED_TEMPERATURES.associateWith { TemperatureResultUiState.Idle }
)
