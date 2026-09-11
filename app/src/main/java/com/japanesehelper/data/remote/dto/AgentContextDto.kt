package com.japanesehelper.data.remote.dto

data class AgentStrategyRequestDto(
    val strategy: String
)

data class AgentStrategyResponseDto(
    val strategy: String
)

data class AgentContextResponseDto(
    val strategy: String,
    val branch: String,
    val branches: List<String>,
    val checkpoints: List<String>,
    val facts: Map<String, String>,
    val messages: List<AgentHistoryMessageDto>
)
