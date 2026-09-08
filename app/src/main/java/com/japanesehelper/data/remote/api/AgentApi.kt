package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface AgentApi {

    @POST("agent/chat")
    suspend fun chat(
        @Body request: AgentChatRequestDto
    ): AgentChatResponseDto

    @GET("agent/history")
    suspend fun getHistory(): AgentHistoryResponseDto

    @DELETE("agent/history")
    suspend fun clearHistory(): AgentHistoryResponseDto
}
