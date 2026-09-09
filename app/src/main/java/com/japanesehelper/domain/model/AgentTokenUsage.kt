package com.japanesehelper.domain.model

/** Real token counts from the backend for one /agent/chat call. A field is
 * null only when Gemini didn't report that number - never an estimate.
 */
data class AgentTokenUsage(
    val currentRequestTokens: Int?,
    val historyTokens: Int?,
    val responseTokens: Int?,
    val totalTokens: Int?
)
