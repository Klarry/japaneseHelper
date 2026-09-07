package com.japanesehelper.presentation.viewmodel.screendata

sealed class AgentChatUiState {
    data object Idle : AgentChatUiState()
    data object Loading : AgentChatUiState()
    data class Success(val response: String) : AgentChatUiState()
    data class Error(val message: String) : AgentChatUiState()
}

data class AiAgentScreenState(
    val message: String = "",
    val result: AgentChatUiState = AgentChatUiState.Idle
)
