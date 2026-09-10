package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentCompressionDto(
    val enabled: Boolean,
    @SerializedName("summary_tokens") val summaryTokens: Int?,
    @SerializedName("messages_sent") val messagesSent: Int
)
