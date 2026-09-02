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
 * Lazy loading: only the initially active tab is fetched on open (see
 * [init]). Switching to another tab fetches it the first time only - once a
 * tab reaches [TabUiState.Success] it acts as its own cache and is shown as-is
 * on every later visit, keyed implicitly by kanji (fixed for this screen
 * instance, taken from the nav argument) and [ExperimentType]. A failed tab
 * does not affect the others and can be retried independently.
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
        loadTab(_state.value.selectedTab)
    }

    /** Switches the active tab and lazily loads it if it has no cached result yet. */
    fun selectTab(experimentType: ExperimentType) {
        _state.value = _state.value.copy(selectedTab = experimentType)
        loadTab(experimentType)
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
