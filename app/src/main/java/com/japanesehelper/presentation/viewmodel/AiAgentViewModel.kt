package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState
import com.japanesehelper.presentation.viewmodel.screendata.AiAgentScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * The AI Agent screen is a simple chat: history is loaded once on open,
 * every sent message is appended (with the agent's reply) to the same list,
 * and "Clear History" empties it. The user's message is always sent to the
 * backend unchanged - no prompt is built here, and conversation context is
 * entirely the backend JapaneseLearningAgent's responsibility.
 */
@HiltViewModel
class AiAgentViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AiAgentScreenState())
    val state: StateFlow<AiAgentScreenState> = _state

    init {
        loadHistory()
    }

    fun loadHistory() {
        _state.value = _state.value.copy(history = AgentHistoryUiState.Loading)

        viewModelScope.launch {
            try {
                val messages = agentRepository.getHistory()
                _state.value = _state.value.copy(history = AgentHistoryUiState.Loaded(messages))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(history = AgentHistoryUiState.Error(e.toErrorMessage()))
            }
        }
    }

    fun onMessageChanged(message: String) {
        _state.value = _state.value.copy(message = message)
    }

    /** Picks the mode for the next request. Nothing is compressed here - the
     * choice is only forwarded, and the backend does the work. */
    fun onCompressionEnabledChanged(enabled: Boolean) {
        _state.value = _state.value.copy(compressionEnabled = enabled)
    }

    fun send() {
        val message = _state.value.message.trim()

        if (message.isEmpty()) return
        if (_state.value.isSending) return

        // Read once, so a mode switched while this request is in flight
        // applies to the next one instead of mislabelling this one.
        val compressionEnabled = _state.value.compressionEnabled

        _state.value = _state.value.copy(isSending = true, sendError = null)

        viewModelScope.launch {
            try {
                val reply = agentRepository.chat(message, compressionEnabled)
                val updatedMessages = currentMessages() +
                    AgentMessage(role = AgentMessageRole.USER, content = message) +
                    AgentMessage(role = AgentMessageRole.ASSISTANT, content = reply.text)

                _state.value = _state.value.copy(
                    message = "",
                    isSending = false,
                    history = AgentHistoryUiState.Loaded(updatedMessages),
                    lastUsage = reply.usage,
                    lastCompression = reply.compression
                )
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(isSending = false, sendError = e.toErrorMessage())
            }
        }
    }

    fun clearHistory() {
        if (_state.value.isClearingHistory) return

        _state.value = _state.value.copy(isClearingHistory = true, clearHistoryError = null)

        viewModelScope.launch {
            try {
                agentRepository.clearHistory()
                _state.value = _state.value.copy(
                    isClearingHistory = false,
                    history = AgentHistoryUiState.Loaded(emptyList()),
                    lastUsage = null,
                    lastCompression = null
                )
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(
                    isClearingHistory = false,
                    clearHistoryError = e.toErrorMessage()
                )
            }
        }
    }

    private fun currentMessages(): List<AgentMessage> =
        (_state.value.history as? AgentHistoryUiState.Loaded)?.messages.orEmpty()

    /** HttpException.message is only "HTTP 400 Bad Request"; the reason is in the body. */
    private fun Exception.toErrorMessage(): String {
        if (this is HttpException) {
            val body = response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) return body
        }
        return message.orEmpty()
    }
}
