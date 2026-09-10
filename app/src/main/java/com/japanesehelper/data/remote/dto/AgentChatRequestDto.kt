package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentChatRequestDto(
    val message: String,
    /** Which mode to answer in. The compressing itself happens entirely on
     * the backend; this only states the choice made on screen. */
    @SerializedName("compression_enabled") val compressionEnabled: Boolean
)
