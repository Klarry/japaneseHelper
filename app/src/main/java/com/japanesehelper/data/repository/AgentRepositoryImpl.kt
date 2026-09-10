package com.japanesehelper.data.repository

import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.api.AgentApi
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.repository.AgentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AgentRepositoryImpl @Inject constructor(
    private val agentApi: AgentApi
) : AgentRepository {

    override suspend fun chat(message: String, compressionEnabled: Boolean): AgentReply =
        withContext(Dispatchers.IO) {
            agentApi.chat(
                AgentChatRequestDto(message = message, compressionEnabled = compressionEnabled)
            ).toDomain()
        }

    override suspend fun getHistory(): List<AgentMessage> = withContext(Dispatchers.IO) {
        agentApi.getHistory().toDomain()
    }

    override suspend fun clearHistory(): Unit = withContext(Dispatchers.IO) {
        agentApi.clearHistory()
        Unit
    }
}
