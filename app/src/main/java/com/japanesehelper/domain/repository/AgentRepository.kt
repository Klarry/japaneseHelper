package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentReply

interface AgentRepository {
    suspend fun chat(message: String): AgentReply
    suspend fun getHistory(): List<AgentMessage>
    suspend fun clearHistory()
}
