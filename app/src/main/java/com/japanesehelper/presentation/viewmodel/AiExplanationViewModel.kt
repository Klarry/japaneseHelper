package com.japanesehelper.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.data.remote.DescriptionPrompts
import com.japanesehelper.domain.repository.DescriptionRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionError
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionLoading
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionState
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiExplanationViewModel @Inject constructor(
    private val descriptionRepository: DescriptionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val DEMO_LOG_TAG = "ImageDescription"
        const val DEMO_LOG_MODE_UNCONTROLLED = "UNCONTROLLED"
        const val DEMO_LOG_MODE_CONTROLLED = "CONTROLLED"
        val DEMO_LOG_DIVIDER = "─".repeat(40)
    }

    val word: String = savedStateHandle.get<String>(Screens.AiExplanation.ARG_WORD).orEmpty()
    private val meaning: String = savedStateHandle.get<String>(Screens.AiExplanation.ARG_MEANING).orEmpty()

    private val _state = MutableStateFlow<DescriptionState>(DescriptionLoading())
    val state: StateFlow<DescriptionState> = _state

    init {
        loadDescription()
    }

    fun retry() {
        loadDescription()
    }

    private fun loadDescription() {
        _state.value = DescriptionLoading()

        viewModelScope.launch {
            try {
                Log.d(DEMO_LOG_TAG, DEMO_LOG_DIVIDER)
                Log.d(DEMO_LOG_TAG, "→ Word: $word")
                Log.d(DEMO_LOG_TAG, "→ Meaning: \"$meaning\"")
                Log.d(DEMO_LOG_TAG, "→ POST /description")
                Log.d(DEMO_LOG_TAG, "→ LLM: ${DescriptionPrompts.PROVIDER}")
                Log.d(DEMO_LOG_TAG, "→ Mode: $DEMO_LOG_MODE_UNCONTROLLED")
                Log.d(DEMO_LOG_TAG, "→ Prompt: \"${DescriptionPrompts.uncontrolled(meaning).asLogLine()}\"")
                Log.d(DEMO_LOG_TAG, "→ Mode: $DEMO_LOG_MODE_CONTROLLED")
                Log.d(DEMO_LOG_TAG, "→ Prompt: \"${DescriptionPrompts.controlled(meaning).asLogLine()}\"")
                Log.d(DEMO_LOG_TAG, "→ Constraints: ${DescriptionPrompts.CONSTRAINTS}")

                val result = descriptionRepository.getDescription(meaning)

                Log.d(DEMO_LOG_TAG, "← 200 OK")
                Log.d(DEMO_LOG_TAG, "← Mode: $DEMO_LOG_MODE_UNCONTROLLED")
                Log.d(DEMO_LOG_TAG, "← Response: ${result.uncontrolled.asLogLine()}")
                Log.d(DEMO_LOG_TAG, "← Mode: $DEMO_LOG_MODE_CONTROLLED")
                Log.d(DEMO_LOG_TAG, "← Response: ${result.controlled.asLogLine()}")
                Log.d(DEMO_LOG_TAG, DEMO_LOG_DIVIDER)

                _state.value = DescriptionSuccess(result.uncontrolled, result.controlled)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = DescriptionError(e.message.orEmpty())
                Log.d(
                    "AiExplanationViewModel",
                    "Exception thrown: ${ e.message }, cause: ${ e.cause }, localizedMessage: ${ e.localizedMessage }"
                )
                e.printStackTrace()
            }
        }
    }

    private fun String.asLogLine(): String = trim().replace(Regex("\\s+"), " ")
}
