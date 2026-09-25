package com.japanesehelper.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class AgentChatResponseDto(
    val response: String,
    val usage: AgentUsageDto,
    /** Nullable: a backend from before Day 17 does not send it at all. */
    @SerializedName("tool_calls") val toolCalls: List<AgentToolCallDto>? = null
)

/**
 * One entry of ``tool_calls``: which tool the backend called and how it went.
 *
 * ``result`` is the tool's own output, and its shape is the tool's business -
 * the backend documents it as an object, a string or nothing at all. It is
 * kept as a raw JsonElement rather than a typed model for exactly that
 * reason: a tool that answers with a string must not break the parsing of
 * the answer it belongs to. The mapper reads the few fields the pipeline
 * readout shows and ignores the rest.
 */
data class AgentToolCallDto(
    @SerializedName("tool") val tool: String?,
    /** Which MCP server the backend routed the call to (Day 20). Absent from
     * a backend from before that, which is why it is nullable. */
    @SerializedName("server") val server: String? = null,
    @SerializedName("arguments") val arguments: Map<String, Any?>?,
    @SerializedName("ok") val ok: Boolean?,
    @SerializedName("error") val error: String?,
    @SerializedName("result") val result: JsonElement? = null
)
