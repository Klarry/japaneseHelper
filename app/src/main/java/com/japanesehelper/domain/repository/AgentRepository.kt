package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentDigest
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentInvariant
import com.japanesehelper.domain.model.AgentInvariantCategory
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMemoryLayer
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.model.AgentTaskStage
import com.japanesehelper.domain.model.AgentTaskState
import com.japanesehelper.domain.model.AgentUserProfile

interface AgentRepository {
    suspend fun chat(message: String, strategy: AgentContextStrategy): AgentReply
    suspend fun getHistory(): List<AgentMessage>
    suspend fun clearHistory()
    suspend fun setStrategy(strategy: AgentContextStrategy)
    suspend fun getContext(): AgentContext
    suspend fun createCheckpoint(): String
    suspend fun createBranch(name: String, checkpoint: String)
    suspend fun switchBranch(name: String)
    suspend fun getMemory(): AgentMemory
    suspend fun clearMemoryLayer(layer: AgentMemoryLayer): AgentMemory
    suspend fun getProfile(): AgentUserProfile
    suspend fun updateProfile(profile: AgentUserProfile): AgentUserProfile
    /** What the backend's periodic digest task has collected so far. */
    suspend fun getDigest(): AgentDigest

    suspend fun getTaskState(): AgentTaskState
    suspend fun clearTaskState(): AgentTaskState

    /**
     * Ask the backend to move the task to [stage].
     *
     * Returns the new state when the move is allowed, and throws
     * [com.japanesehelper.domain.model.AgentTaskTransitionRefused] with the
     * backend's own explanation when it is not. Nothing decides that here.
     */
    suspend fun requestTaskTransition(stage: AgentTaskStage): AgentTaskState

    /** Record the plan the task will be executed by - the backend's
     * condition for leaving planning. */
    suspend fun approveTaskPlan(plan: String): AgentTaskState

    /** Record how validation went - the backend's condition for done. */
    suspend fun recordTaskValidation(passed: Boolean, notes: String = ""): AgentTaskState
    suspend fun getInvariants(): List<AgentInvariant>
    suspend fun saveInvariant(
        id: String?,
        category: AgentInvariantCategory,
        rule: String
    ): List<AgentInvariant>
    suspend fun deleteInvariant(id: String): List<AgentInvariant>
}
