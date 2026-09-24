package com.japanesehelper.domain.model

data class AgentReply(
    val text: String,
    val usage: AgentTokenUsage,
    /** The MCP tools the backend agent called for this answer, in order.
     * Empty when it answered without looking anything up. */
    val toolCalls: List<AgentToolCall> = emptyList(),
    /** Set when those calls were the backend's search -> summarize ->
     * save_to_file chain rather than a single lookup. */
    val pipeline: AgentPipeline? = null
)
