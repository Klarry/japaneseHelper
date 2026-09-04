package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.ModelComparison

sealed class ModelComparisonResultUiState {
    data object Idle : ModelComparisonResultUiState()
    data object Loading : ModelComparisonResultUiState()
    data class Success(val comparison: ModelComparison) : ModelComparisonResultUiState()
    data class Error(val message: String) : ModelComparisonResultUiState()
}

data class ModelComparisonScreenState(
    val kanji: String,
    val furigana: String = "",
    val meaning: String = "",
    val result: ModelComparisonResultUiState = ModelComparisonResultUiState.Idle
)
