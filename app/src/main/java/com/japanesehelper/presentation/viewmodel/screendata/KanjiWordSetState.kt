package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.ExperimentType
import com.japanesehelper.domain.model.KanjiWordSet

/**
 * Independent state of a single experiment tab.
 *
 * [Idle] doubles as "no cache yet" - selecting a tab in this state triggers
 * the API request. [Success] doubles as the cache: it is never refetched.
 */
sealed class TabUiState {
    data object Idle : TabUiState()
    data object Loading : TabUiState()
    data class Success(val result: KanjiWordSet) : TabUiState()
    data class Error(val message: String) : TabUiState()
}

/**
 * UI state for the Kanji Word Set screen.
 *
 * @param kanji The dynamic kanji this screen instance was opened for.
 * @param selectedTab The currently active experiment tab.
 * @param tabs Per-experiment state. Acts as the cache: an entry already in
 * [TabUiState.Success] is shown as-is and never refetched.
 */
data class KanjiWordSetScreenState(
    val kanji: String,
    val selectedTab: ExperimentType = ExperimentType.entries.first(),
    val tabs: Map<ExperimentType, TabUiState> = ExperimentType.entries.associateWith { TabUiState.Idle }
)
