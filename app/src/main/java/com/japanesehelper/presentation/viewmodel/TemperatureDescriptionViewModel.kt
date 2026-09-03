package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.repository.TemperatureDescriptionRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureDescriptionScreenState
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureResultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Drives the Temperature Description screen: one section per supported
 * temperature, each with its own independent [TemperatureResultUiState].
 *
 * Unlike Kanji Word Set, nothing runs automatically when the screen opens -
 * every section starts at [TemperatureResultUiState.Idle] and only requests
 * its result once the user taps that section's "Run experiment" button (see
 * [run]). Running one temperature never affects the others.
 *
 * [kanji], [furigana] and [meaning] all come from the nav argument, never
 * hardcoded.
 */
@HiltViewModel
class TemperatureDescriptionViewModel @Inject constructor(
    private val temperatureDescriptionRepository: TemperatureDescriptionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val kanji: String = savedStateHandle.get<String>(Screens.TemperatureDescription.ARG_KANJI).orEmpty()
    val furigana: String = savedStateHandle.get<String>(Screens.TemperatureDescription.ARG_FURIGANA).orEmpty()
    val meaning: String = savedStateHandle.get<String>(Screens.TemperatureDescription.ARG_MEANING).orEmpty()

    private val _state = MutableStateFlow(
        TemperatureDescriptionScreenState(kanji = kanji, furigana = furigana, meaning = meaning)
    )
    val state: StateFlow<TemperatureDescriptionScreenState> = _state

    /**
     * Runs (or re-runs, e.g. after an [TemperatureResultUiState.Error]) the
     * experiment for one [temperature]. Ignored while that temperature is
     * already loading, so a double-tap never fires a duplicate request.
     */
    fun run(temperature: Double) {
        if (_state.value.results[temperature] is TemperatureResultUiState.Loading) return

        updateResult(temperature, TemperatureResultUiState.Loading)

        viewModelScope.launch {
            try {
                val result = temperatureDescriptionRepository.getTemperatureDescription(kanji, temperature)
                updateResult(temperature, TemperatureResultUiState.Success(result))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                updateResult(temperature, TemperatureResultUiState.Error(e.toErrorMessage()))
            }
        }
    }

    /**
     * A plain [HttpException.message] is just "HTTP 400 Bad Request" - the
     * actual reason (e.g. why the backend rejected the request) lives in the
     * response body, so read that instead when it's present.
     */
    private fun Exception.toErrorMessage(): String {
        if (this is HttpException) {
            val body = response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) return body
        }
        return message.orEmpty()
    }

    private fun updateResult(temperature: Double, newState: TemperatureResultUiState) {
        _state.value = _state.value.copy(results = _state.value.results + (temperature to newState))
    }
}
