package com.japanesehelper.data.remote.dto

data class AgentInvariantDto(
    val id: String,
    val category: String,
    val rule: String
)

data class AgentInvariantsResponseDto(
    val invariants: List<AgentInvariantDto>
)

data class AgentInvariantRequestDto(
    val category: String,
    val rule: String
)
