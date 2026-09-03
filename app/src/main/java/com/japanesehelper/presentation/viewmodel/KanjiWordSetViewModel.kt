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

@HiltViewModel
class KanjiWordSetViewModel @Inject constructor(
    private val kanjiWordSetRepository: KanjiWordSetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val kanji: String = savedStateHandle.get<String>(Screens.KanjiWordSet.ARG_KANJI).orEmpty()

    private val _state = MutableStateFlow(KanjiWordSetScreenState(kanji = kanji))
    val state: StateFlow<KanjiWordSetScreenState> = _state

    init {
        ExperimentType.entries.forEach(::loadTab)
    }

    fun selectTab(experimentType: ExperimentType) {
        _state.value = _state.value.copy(selectedTab = experimentType)
    }

    fun retry(experimentType: ExperimentType) {
        loadTab(experimentType)
    }

    private fun loadTab(experimentType: ExperimentType) {
        // Success doubles as the cache; Loading means a request is already in flight.
        val currentTabState = _state.value.tabs[experimentType]
        if (currentTabState is TabUiState.Success || currentTabState is TabUiState.Loading) return

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
