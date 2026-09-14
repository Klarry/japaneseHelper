package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentContextResponseDto
import com.japanesehelper.data.remote.dto.AgentHistoryMessageDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import com.japanesehelper.data.remote.dto.AgentLongTermMemoryDto
import com.japanesehelper.data.remote.dto.AgentMemoryResponseDto
import com.japanesehelper.data.remote.dto.AgentShortTermMemoryDto
import com.japanesehelper.data.remote.dto.AgentWorkingMemoryDto
import com.japanesehelper.data.remote.dto.AgentUsageDto
import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentLongTermMemory
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.model.AgentShortTermMemory
import com.japanesehelper.domain.model.AgentTokenUsage
import com.japanesehelper.domain.model.AgentWorkingMemory

fun AgentChatResponseDto.toDomain(): AgentReply {
    return AgentReply(text = response, usage = usage.toDomain())
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

fun AgentContextResponseDto.toDomain(): AgentContext {
    return AgentContext(
        strategy = strategy,
        branch = branch,
        branches = branches,
        checkpoints = checkpoints,
        facts = facts,
        messages = messages.map { it.toDomain() }
    )
}

fun AgentShortTermMemoryDto.toDomain(): AgentShortTermMemory =
    AgentShortTermMemory(messages = messages.map { it.toDomain() })

fun AgentWorkingMemoryDto.toDomain(): AgentWorkingMemory = AgentWorkingMemory(
    goals = goals,
    requirements = requirements,
    constraints = constraints,
    decisions = decisions
)

fun AgentLongTermMemoryDto.toDomain(): AgentLongTermMemory = AgentLongTermMemory(
    profile = profile,
    preferences = preferences,
    decisions = decisions,
    knowledge = knowledge
)

fun AgentMemoryResponseDto.toDomain(): AgentMemory = AgentMemory(
    shortTerm = shortTerm.toDomain(),
    working = working.toDomain(),
    longTerm = longTerm.toDomain()
)
