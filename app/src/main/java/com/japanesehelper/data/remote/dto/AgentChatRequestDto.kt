package com.japanesehelper.data.remote.dto

data class AgentChatRequestDto(
    val message: String,
    /** Which context strategy to answer with. The strategy itself runs on the
     * backend; this only states the choice made on screen. */
    val strategy: String
)
