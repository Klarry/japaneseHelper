package com.japanesehelper.data.remote.dto

data class AgentHistoryMessageDto(
    val role: String,
    val content: String
)

data class AgentHistoryResponseDto(
    val messages: List<AgentHistoryMessageDto>
)
