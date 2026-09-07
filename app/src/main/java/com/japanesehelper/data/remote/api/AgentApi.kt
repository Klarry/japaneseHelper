package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AgentApi {

    @POST("agent/chat")
    suspend fun chat(
        @Body request: AgentChatRequestDto
    ): AgentChatResponseDto
}
