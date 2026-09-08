package com.japanesehelper.domain.model

enum class AgentMessageRole { USER, ASSISTANT }

data class AgentMessage(
    val role: AgentMessageRole,
    val content: String
)
