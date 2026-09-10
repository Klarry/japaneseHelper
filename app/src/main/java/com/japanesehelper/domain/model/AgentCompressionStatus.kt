package com.japanesehelper.domain.model

/**
 * What the backend is keeping for the conversation after the last request:
 * whether it compressed the history, how big the stored summary is, and how
 * many messages it still keeps word for word. All of it is measured and
 * decided on the backend - the device only reports it.
 */
data class AgentCompressionStatus(
    val enabled: Boolean,
    val summaryTokens: Int?,
    val recentMessages: Int
)
