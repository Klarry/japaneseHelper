package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.AgentBranchRequestDto
import com.japanesehelper.data.remote.dto.AgentBranchResponseDto
import com.japanesehelper.data.remote.dto.AgentBranchSwitchRequestDto
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentCheckpointResponseDto
import com.japanesehelper.data.remote.dto.AgentContextResponseDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import com.japanesehelper.data.remote.dto.AgentInvariantRequestDto
import com.japanesehelper.data.remote.dto.AgentInvariantsResponseDto
import com.japanesehelper.data.remote.dto.AgentMemoryResponseDto
import com.japanesehelper.data.remote.dto.AgentUserProfileDto
import com.japanesehelper.data.remote.dto.AgentUserProfileRequestDto
import com.japanesehelper.data.remote.dto.AgentStrategyRequestDto
import com.japanesehelper.data.remote.dto.AgentStrategyResponseDto
import com.japanesehelper.data.remote.dto.AgentTaskStateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("agent/memory")
    suspend fun getMemory(): AgentMemoryResponseDto

    /** Empties one layer. The other two are left exactly as they are - which
     * is the backend's guarantee, not something checked here. */
    @DELETE("agent/memory/{layer}")
    suspend fun clearMemoryLayer(
        @Path("layer") layer: String
    ): AgentMemoryResponseDto

    @GET("agent/profile")
    suspend fun getProfile(): AgentUserProfileDto

    @PUT("agent/profile")
    suspend fun updateProfile(
        @Body request: AgentUserProfileRequestDto
    ): AgentUserProfileDto

    @GET("agent/task")
    suspend fun getTaskState(): AgentTaskStateDto

    @DELETE("agent/task")
    suspend fun clearTaskState(): AgentTaskStateDto

    @GET("agent/invariants")
    suspend fun getInvariants(): AgentInvariantsResponseDto

    @POST("agent/invariants")
    suspend fun addInvariant(
        @Body request: AgentInvariantRequestDto
    ): AgentInvariantsResponseDto

    @PUT("agent/invariants/{id}")
    suspend fun setInvariant(
        @Path("id") id: String,
        @Body request: AgentInvariantRequestDto
    ): AgentInvariantsResponseDto

    @DELETE("agent/invariants/{id}")
    suspend fun deleteInvariant(
        @Path("id") id: String
    ): AgentInvariantsResponseDto
}
