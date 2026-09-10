package com.japanesehelper.domain.model

/**
 * The context the backend sent for the last request: whether it compressed
 * the history, how big the summary that went with it was, and how many
 * messages went along word for word. It describes that request - the same one
 * the token usage does - not the conversation as it stands afterwards. All of
 * it is measured and decided on the backend; the device only reports it.
 */
data class AgentCompressionStatus(
    val enabled: Boolean,
    val summaryTokens: Int?,
    val messagesSent: Int
)
