package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentHistoryMessageDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentReply

fun AgentChatResponseDto.toDomain(): AgentReply {
    return AgentReply(text = response)
}

fun AgentHistoryMessageDto.toDomain(): AgentMessage {
    val mappedRole = if (role == "user") AgentMessageRole.USER else AgentMessageRole.ASSISTANT
    return AgentMessage(role = mappedRole, content = content)
}

fun AgentHistoryResponseDto.toDomain(): List<AgentMessage> = messages.map { it.toDomain() }
