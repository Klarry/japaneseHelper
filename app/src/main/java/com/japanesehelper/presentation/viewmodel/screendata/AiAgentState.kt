package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.AgentMessage

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
    val clearHistoryError: String? = null
)
