package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.domain.model.AgentReply

fun AgentChatResponseDto.toDomain(): AgentReply {
    return AgentReply(text = response)
}
