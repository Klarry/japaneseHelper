package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentTokenUsage

sealed class AgentHistoryUiState {
    data object Loading : AgentHistoryUiState()
    data class Error(val message: String) : AgentHistoryUiState()
    data class Loaded(val messages: List<AgentMessage>) : AgentHistoryUiState()
}

data class AiAgentScreenState(
    val message: String = "",
    val history: AgentHistoryUiState = AgentHistoryUiState.Loading,
    val isSending: Boolean = false,
    val sendError: String? = null,
    val isClearingHistory: Boolean = false,
    val clearHistoryError: String? = null,
    /** Token usage for the last successful send this session - not part of
     * persisted history, so it starts empty on every screen open. */
    val lastUsage: AgentTokenUsage? = null
)
