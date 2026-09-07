package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.AgentReply

interface AgentRepository {
    suspend fun chat(message: String): AgentReply
}
