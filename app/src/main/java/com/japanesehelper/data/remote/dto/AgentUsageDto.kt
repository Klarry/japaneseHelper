package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentUsageDto(
    @SerializedName("current_request_tokens") val currentRequestTokens: Int?,
    @SerializedName("history_tokens") val historyTokens: Int?,
    @SerializedName("response_tokens") val responseTokens: Int?,
    @SerializedName("total_tokens") val totalTokens: Int?
)
