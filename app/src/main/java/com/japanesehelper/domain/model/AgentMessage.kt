package com.japanesehelper.domain.model

enum class AgentMessageRole { USER, ASSISTANT }

data class AgentMessage(
    val role: AgentMessageRole,
    val content: String,
    /** The MCP tools behind an answer given in this session. The backend does
     * not store them with the conversation, so answers read back from history
     * have none - the same way token usage lives only for the session. */
    val toolCalls: List<AgentToolCall> = emptyList(),
    /** The chain behind this answer, when it was one. Like the tool calls, it
     * lives only for this session. */
    val pipeline: AgentPipeline? = null
)
