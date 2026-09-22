package com.japanesehelper.domain.model

/**
 * One MCP tool call the backend agent made while answering a message.
 *
 * Reported, not made: the device never talks MCP. The backend decides that a
 * message needs a lookup, calls the tool and writes the answer with what came
 * back; this is what it says it called, so the screen can show it next to
 * the answer it was used for.
 */
data class AgentToolCall(
    val tool: String,
    val arguments: Map<String, String> = emptyMap(),
    val ok: Boolean = true,
    val error: String = ""
)
