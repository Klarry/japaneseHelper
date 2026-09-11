package com.japanesehelper.data.remote.dto

data class AgentCheckpointResponseDto(
    val name: String,
    val branch: String,
    val messages: Int
)

data class AgentBranchRequestDto(
    val name: String,
    val checkpoint: String
)

data class AgentBranchSwitchRequestDto(
    val name: String
)

data class AgentBranchResponseDto(
    val branch: String,
    val branches: List<String>
)
