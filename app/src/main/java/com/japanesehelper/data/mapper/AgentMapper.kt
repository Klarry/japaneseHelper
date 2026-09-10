package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentCompressionDto
import com.japanesehelper.data.remote.dto.AgentHistoryMessageDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import com.japanesehelper.data.remote.dto.AgentUsageDto
import com.japanesehelper.domain.model.AgentCompressionStatus
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.model.AgentTokenUsage

fun AgentChatResponseDto.toDomain(): AgentReply {
    return AgentReply(
        text = response,
        usage = usage.toDomain(),
        compression = compression.toDomain()
    )
}

fun AgentCompressionDto.toDomain(): AgentCompressionStatus {
    return AgentCompressionStatus(
        enabled = enabled,
        summaryTokens = summaryTokens,
        recentMessages = recentMessages
    )
}

fun AgentUsageDto.toDomain(): AgentTokenUsage {
    return AgentTokenUsage(
        currentRequestTokens = currentRequestTokens,
        historyTokens = historyTokens,
        responseTokens = responseTokens,
        totalTokens = totalTokens
    )
}

fun AgentHistoryMessageDto.toDomain(): AgentMessage {
    val mappedRole = if (role == "user") AgentMessageRole.USER else AgentMessageRole.ASSISTANT
    return AgentMessage(role = mappedRole, content = content)
}

fun AgentHistoryResponseDto.toDomain(): List<AgentMessage> = messages.map { it.toDomain() }
