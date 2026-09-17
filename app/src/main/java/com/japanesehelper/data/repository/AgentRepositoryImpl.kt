package com.japanesehelper.data.repository

import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.mapper.toRequestDto
import com.japanesehelper.data.remote.api.AgentApi
import com.japanesehelper.data.remote.dto.AgentBranchRequestDto
import com.japanesehelper.data.remote.dto.AgentBranchSwitchRequestDto
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentInvariantRequestDto
import com.japanesehelper.data.remote.dto.AgentStrategyRequestDto
import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentInvariant
import com.japanesehelper.domain.model.AgentInvariantCategory
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMemoryLayer
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.model.AgentTaskState
import com.japanesehelper.domain.model.AgentUserProfile
import com.japanesehelper.domain.repository.AgentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AgentRepositoryImpl @Inject constructor(
    private val agentApi: AgentApi
) : AgentRepository {

    override suspend fun chat(message: String, strategy: AgentContextStrategy): AgentReply =
        withContext(Dispatchers.IO) {
            agentApi.chat(AgentChatRequestDto(message = message, strategy = strategy.wireName)).toDomain()
        }

    override suspend fun getHistory(): List<AgentMessage> = withContext(Dispatchers.IO) {
        agentApi.getHistory().toDomain()
    }

    override suspend fun clearHistory(): Unit = withContext(Dispatchers.IO) {
        agentApi.clearHistory()
        Unit
    }

    override suspend fun setStrategy(strategy: AgentContextStrategy): Unit = withContext(Dispatchers.IO) {
        agentApi.setStrategy(AgentStrategyRequestDto(strategy = strategy.wireName))
        Unit
    }

    override suspend fun getContext(): AgentContext = withContext(Dispatchers.IO) {
        agentApi.getContext().toDomain()
    }

    override suspend fun createCheckpoint(): String = withContext(Dispatchers.IO) {
        agentApi.createCheckpoint().name
    }

    override suspend fun createBranch(name: String, checkpoint: String): Unit = withContext(Dispatchers.IO) {
        agentApi.createBranch(AgentBranchRequestDto(name = name, checkpoint = checkpoint))
        Unit
    }

    override suspend fun switchBranch(name: String): Unit = withContext(Dispatchers.IO) {
        agentApi.switchBranch(AgentBranchSwitchRequestDto(name = name))
        Unit
    }

    override suspend fun getMemory(): AgentMemory = withContext(Dispatchers.IO) {
        agentApi.getMemory().toDomain()
    }

    override suspend fun clearMemoryLayer(layer: AgentMemoryLayer): AgentMemory =
        withContext(Dispatchers.IO) {
            agentApi.clearMemoryLayer(layer.wireName).toDomain()
        }

    override suspend fun getProfile(): AgentUserProfile = withContext(Dispatchers.IO) {
        agentApi.getProfile().toDomain()
    }

    override suspend fun updateProfile(profile: AgentUserProfile): AgentUserProfile =
        withContext(Dispatchers.IO) {
            agentApi.updateProfile(profile.toRequestDto()).toDomain()
        }

    override suspend fun getTaskState(): AgentTaskState = withContext(Dispatchers.IO) {
        agentApi.getTaskState().toDomain()
    }

    override suspend fun clearTaskState(): AgentTaskState = withContext(Dispatchers.IO) {
        agentApi.clearTaskState().toDomain()
    }

    override suspend fun getInvariants(): List<AgentInvariant> = withContext(Dispatchers.IO) {
        agentApi.getInvariants().toDomain()
    }

    /** No id means a new rule, which the backend names itself. */
    override suspend fun saveInvariant(
        id: String?,
        category: AgentInvariantCategory,
        rule: String
    ): List<AgentInvariant> = withContext(Dispatchers.IO) {
        val request = AgentInvariantRequestDto(category = category.wireName, rule = rule)
        val response = if (id == null) agentApi.addInvariant(request) else agentApi.setInvariant(id, request)
        response.toDomain()
    }

    override suspend fun deleteInvariant(id: String): List<AgentInvariant> = withContext(Dispatchers.IO) {
        agentApi.deleteInvariant(id).toDomain()
    }
}
