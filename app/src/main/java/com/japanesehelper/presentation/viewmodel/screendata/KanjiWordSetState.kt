package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.ExperimentType
import com.japanesehelper.domain.model.KanjiWordSet

sealed class TabUiState {
    data object Idle : TabUiState()
    data object Loading : TabUiState()
    data class Success(val result: KanjiWordSet) : TabUiState()
    data class Error(val message: String) : TabUiState()
}

data class KanjiWordSetScreenState(
    val kanji: String,
    val selectedTab: ExperimentType = ExperimentType.entries.first(),
    val tabs: Map<ExperimentType, TabUiState> = ExperimentType.entries.associateWith { TabUiState.Idle }
)
