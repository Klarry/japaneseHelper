package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentShortTermMemoryDto(
    val messages: List<AgentHistoryMessageDto>
)

data class AgentWorkingMemoryDto(
    val goals: List<String>,
    val requirements: List<String>,
    val constraints: List<String>,
    val decisions: List<String>
)

data class AgentLongTermMemoryDto(
    val profile: Map<String, String>,
    val preferences: List<String>,
    val decisions: List<String>,
    val knowledge: List<String>
)

data class AgentMemoryResponseDto(
    @SerializedName("short_term") val shortTerm: AgentShortTermMemoryDto,
    val working: AgentWorkingMemoryDto,
    @SerializedName("long_term") val longTerm: AgentLongTermMemoryDto
)
