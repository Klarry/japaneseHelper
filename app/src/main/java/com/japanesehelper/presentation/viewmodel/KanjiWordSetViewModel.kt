package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.model.ExperimentType
import com.japanesehelper.domain.repository.KanjiWordSetRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.screendata.KanjiWordSetScreenState
import com.japanesehelper.presentation.viewmodel.screendata.TabUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the Kanji Word Set screen: one tab per [ExperimentType], each with
 * its own independent [TabUiState].
 *
 * All four experiments are requested concurrently, each in its own
 * coroutine, as soon as the screen opens (see [init]) - switching tabs never
 * waits on a network response, it just shows whatever that tab's state
 * already is. A tab that reached [TabUiState.Success] acts as its own cache
 * and is never refetched; a failed tab does not affect the others and can be
 * retried independently via [retry].
 */
@HiltViewModel
class KanjiWordSetViewModel @Inject constructor(
    private val kanjiWordSetRepository: KanjiWordSetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val kanji: String = savedStateHandle.get<String>(Screens.KanjiWordSet.ARG_KANJI).orEmpty()

    private val _state = MutableStateFlow(KanjiWordSetScreenState(kanji = kanji))
    val state: StateFlow<KanjiWordSetScreenState> = _state

    init {
        // Fire all four experiments at once, each in its own coroutine, so no
        // tab switch ever has to wait on a request that hasn't started yet.
        ExperimentType.entries.forEach { experimentType -> loadTab(experimentType) }
    }

    /**
     * Switches the active tab. Every tab is already loading or loaded from
     * [init], so this never triggers a request by itself.
     */
    fun selectTab(experimentType: ExperimentType) {
        _state.value = _state.value.copy(selectedTab = experimentType)
    }

    /** Re-runs a tab that previously failed. */
    fun retry(experimentType: ExperimentType) {
        loadTab(experimentType)
    }

    private fun loadTab(experimentType: ExperimentType) {
        val currentTabState = _state.value.tabs[experimentType]

        // Success = cache hit, nothing to do. Loading = already in flight, avoid a duplicate request.
        if (currentTabState is TabUiState.Success || currentTabState is TabUiState.Loading) {
            return
        }

        updateTab(experimentType, TabUiState.Loading)

        viewModelScope.launch {
            try {
                val result = kanjiWordSetRepository.getKanjiWordSet(kanji, experimentType)
                updateTab(experimentType, TabUiState.Success(result))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                updateTab(experimentType, TabUiState.Error(e.message.orEmpty()))
            }
        }
    }

    private fun updateTab(experimentType: ExperimentType, newState: TabUiState) {
        _state.value = _state.value.copy(
            tabs = _state.value.tabs + (experimentType to newState)
        )
    }
}
