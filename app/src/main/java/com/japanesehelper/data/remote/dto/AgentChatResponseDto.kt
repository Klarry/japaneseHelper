package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentChatResponseDto(
    val response: String,
    val usage: AgentUsageDto,
    /** Nullable: a backend from before Day 17 does not send it at all. */
    @SerializedName("tool_calls") val toolCalls: List<AgentToolCallDto>? = null
)

/**
 * One entry of ``tool_calls``. The backend also sends the tool's full result;
 * the screen only says which tool was used, so the result is not read.
 */
data class AgentToolCallDto(
    @SerializedName("tool") val tool: String?,
    @SerializedName("arguments") val arguments: Map<String, Any?>?,
    @SerializedName("ok") val ok: Boolean?,
    @SerializedName("error") val error: String?
)
