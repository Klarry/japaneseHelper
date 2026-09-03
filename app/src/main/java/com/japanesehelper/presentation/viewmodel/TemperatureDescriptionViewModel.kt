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

/** Nothing is requested until the user runs a temperature; each runs independently. */
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

    /** HttpException.message is only "HTTP 400 Bad Request"; the reason is in the body. */
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
