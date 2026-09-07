package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.screendata.AgentChatUiState
import com.japanesehelper.presentation.viewmodel.screendata.AiAgentScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Nothing is requested until the user taps "Ask Agent". The user's message
 * is sent to the backend unchanged - no prompt is built here, that belongs
 * to the backend's JapaneseLearningAgent.
 */
@HiltViewModel
class AiAgentViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AiAgentScreenState())
    val state: StateFlow<AiAgentScreenState> = _state

    fun onMessageChanged(message: String) {
        _state.value = _state.value.copy(message = message)
    }

    fun ask() {
        val message = _state.value.message.trim()

        if (message.isEmpty()) return
        if (_state.value.result is AgentChatUiState.Loading) return

        _state.value = _state.value.copy(result = AgentChatUiState.Loading)

        viewModelScope.launch {
            try {
                val reply = agentRepository.chat(message)
                _state.value = _state.value.copy(result = AgentChatUiState.Success(reply.text))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(result = AgentChatUiState.Error(e.toErrorMessage()))
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
