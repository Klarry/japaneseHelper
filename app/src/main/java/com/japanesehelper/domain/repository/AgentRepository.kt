package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMemoryLayer
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentReply
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
}
