package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.data.remote.MODEL_COMPARISON_PROMPT
import com.japanesehelper.domain.model.COMPARISON_MODELS
import com.japanesehelper.domain.repository.ModelComparisonRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.screendata.ModelComparisonResultUiState
import com.japanesehelper.presentation.viewmodel.screendata.ModelComparisonScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Nothing is requested until the user runs the comparison. A single run sends
 * one request with all of [COMPARISON_MODELS] and the same kanji/prompt - the
 * backend calls the three models and reports back independently per model.
 */
@HiltViewModel
class ModelComparisonViewModel @Inject constructor(
    private val modelComparisonRepository: ModelComparisonRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val kanji: String = savedStateHandle.get<String>(Screens.ModelComparison.ARG_KANJI).orEmpty()
    val furigana: String = savedStateHandle.get<String>(Screens.ModelComparison.ARG_FURIGANA).orEmpty()
    val meaning: String = savedStateHandle.get<String>(Screens.ModelComparison.ARG_MEANING).orEmpty()

    private val _state = MutableStateFlow(
        ModelComparisonScreenState(kanji = kanji, furigana = furigana, meaning = meaning)
    )
    val state: StateFlow<ModelComparisonScreenState> = _state

    fun run() {
        if (_state.value.result is ModelComparisonResultUiState.Loading) return

        _state.value = _state.value.copy(result = ModelComparisonResultUiState.Loading)

        viewModelScope.launch {
            try {
                val result = modelComparisonRepository.compareModels(
                    kanji = kanji,
                    prompt = MODEL_COMPARISON_PROMPT,
                    models = COMPARISON_MODELS.map { it.modelId }
                )
                _state.value = _state.value.copy(result = ModelComparisonResultUiState.Success(result))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(result = ModelComparisonResultUiState.Error(e.toErrorMessage()))
            }
        }
    }

    /** HttpException.message is only "HTTP 400 Bad Request"; the reason is in the body. */
    private fun Exception.toErrorMessage(): String {
        if (this is HttpException) {
            val body = response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) return body
        }
        return message.orEmpty()
    }
}
