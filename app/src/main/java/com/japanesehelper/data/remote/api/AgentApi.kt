package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.AgentBranchRequestDto
import com.japanesehelper.data.remote.dto.AgentBranchResponseDto
import com.japanesehelper.data.remote.dto.AgentBranchSwitchRequestDto
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentCheckpointResponseDto
import com.japanesehelper.data.remote.dto.AgentContextResponseDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import com.japanesehelper.data.remote.dto.AgentStrategyRequestDto
import com.japanesehelper.data.remote.dto.AgentStrategyResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AgentApi {

    @POST("agent/chat")
    suspend fun chat(
        @Body request: AgentChatRequestDto
    ): AgentChatResponseDto

    @GET("agent/history")
    suspend fun getHistory(): AgentHistoryResponseDto

    @DELETE("agent/history")
    suspend fun clearHistory(): AgentHistoryResponseDto

    @PUT("agent/strategy")
    suspend fun setStrategy(
        @Body request: AgentStrategyRequestDto
    ): AgentStrategyResponseDto

    @GET("agent/context")
    suspend fun getContext(): AgentContextResponseDto

    /** The backend names the checkpoint, so there is nothing to send. */
    @POST("agent/checkpoint")
    suspend fun createCheckpoint(): AgentCheckpointResponseDto

    @POST("agent/branch")
    suspend fun createBranch(
        @Body request: AgentBranchRequestDto
    ): AgentBranchResponseDto

    @PUT("agent/branch")
    suspend fun switchBranch(
        @Body request: AgentBranchSwitchRequestDto
    ): AgentBranchResponseDto
}
