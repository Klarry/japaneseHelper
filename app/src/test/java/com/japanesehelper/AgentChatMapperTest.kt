package com.japanesehelper

import com.google.gson.Gson
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentContextResponseDto
import com.japanesehelper.domain.model.AgentMessageRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The backend replies with snake_case keys; this locks in the @SerializedName
 * mapping against real Gson parsing, the same way ModelComparisonMapperTest
 * does for model_comparison_service.
 */
class AgentChatMapperTest {

    private val gson = Gson()

    @Test
    fun `snake_case usage fields deserialize and map correctly`() {
        val json = """
            {
              "response": "学 means to study.",
              "usage": {
                "current_request_tokens": 42,
                "history_tokens": 318,
                "response_tokens": 76,
                "total_tokens": 436
              }
            }
        """.trimIndent()

        val domain = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain()

        assertEquals("学 means to study.", domain.text)
        assertEquals(42, domain.usage.currentRequestTokens)
        assertEquals(318, domain.usage.historyTokens)
        assertEquals(76, domain.usage.responseTokens)
        assertEquals(436, domain.usage.totalTokens)
    }

    @Test
    fun `null usage fields the backend could not report deserialize as null`() {
        val json = """
            {
              "response": "answer",
              "usage": {
                "current_request_tokens": null,
                "history_tokens": 10,
                "response_tokens": 6,
                "total_tokens": null
              }
            }
        """.trimIndent()

        val domain = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain()

        assertNull(domain.usage.currentRequestTokens)
        assertEquals(10, domain.usage.historyTokens)
        assertNull(domain.usage.totalTokens)
    }

    @Test
    fun `the request sends the chosen strategy under the key the backend expects`() {
        val json = gson.toJson(AgentChatRequestDto(message = "Explain 学", strategy = "sticky_facts"))

        assertEquals("""{"message":"Explain 学","strategy":"sticky_facts"}""", json)
    }

    @Test
    fun `the context the backend reports deserializes and maps`() {
        val json = """
            {
              "strategy": "sticky_facts",
              "branch": "formal",
              "branches": ["main", "formal"],
              "checkpoints": ["cp-1"],
              "facts": {"goal": "сдать N3", "level": "N4"},
              "messages": [
                {"role": "user", "content": "Расскажи о 学習"},
                {"role": "assistant", "content": "学習 — «учёба»."}
              ],
              "context": "…the whole prompt prefix…"
            }
        """.trimIndent()

        val domain = gson.fromJson(json, AgentContextResponseDto::class.java).toDomain()

        assertEquals("sticky_facts", domain.strategy)
        assertEquals("formal", domain.branch)
        assertEquals(listOf("main", "formal"), domain.branches)
        assertEquals(listOf("cp-1"), domain.checkpoints)
        assertEquals(mapOf("goal" to "сдать N3", "level" to "N4"), domain.facts)
        assertEquals(2, domain.messages.size)
        assertEquals(AgentMessageRole.USER, domain.messages[0].role)
        assertEquals("学習 — «учёба».", domain.messages[1].content)
    }
}
