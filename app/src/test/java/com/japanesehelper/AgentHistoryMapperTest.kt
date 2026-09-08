package com.japanesehelper

import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.AgentHistoryMessageDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import com.japanesehelper.domain.model.AgentMessageRole
import org.junit.Assert.assertEquals
import org.junit.Test

class AgentHistoryMapperTest {

    @Test
    fun `user and assistant roles map to the matching enum values`() {
        val dto = AgentHistoryResponseDto(
            messages = listOf(
                AgentHistoryMessageDto(role = "user", content = "Explain the kanji 学"),
                AgentHistoryMessageDto(role = "assistant", content = "学 means to study.")
            )
        )

        val domain = dto.toDomain()

        assertEquals(AgentMessageRole.USER, domain[0].role)
        assertEquals("Explain the kanji 学", domain[0].content)
        assertEquals(AgentMessageRole.ASSISTANT, domain[1].role)
        assertEquals("学 means to study.", domain[1].content)
    }

    @Test
    fun `an unrecognized role defaults to assistant rather than crashing`() {
        val message = AgentHistoryMessageDto(role = "system", content = "note").toDomain()

        assertEquals(AgentMessageRole.ASSISTANT, message.role)
    }

    @Test
    fun `message order is preserved`() {
        val dto = AgentHistoryResponseDto(
            messages = listOf(
                AgentHistoryMessageDto(role = "user", content = "first"),
                AgentHistoryMessageDto(role = "assistant", content = "second"),
                AgentHistoryMessageDto(role = "user", content = "third")
            )
        )

        val domain = dto.toDomain()

        assertEquals(listOf("first", "second", "third"), domain.map { it.content })
    }

    @Test
    fun `an empty history maps to an empty list`() {
        val domain = AgentHistoryResponseDto(messages = emptyList()).toDomain()

        assertEquals(emptyList<Any>(), domain)
    }
}
