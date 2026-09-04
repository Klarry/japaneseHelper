package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ModelComparisonResultDto(
    val model: String,
    val text: String?,
    @SerializedName("response_time_ms") val responseTimeMs: Int,
    @SerializedName("input_tokens") val inputTokens: Int?,
    @SerializedName("output_tokens") val outputTokens: Int?,
    val error: String?
)
