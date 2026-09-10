package com.japanesehelper.domain.model

data class AgentReply(
    val text: String,
    val usage: AgentTokenUsage,
    val compression: AgentCompressionStatus
)
