package com.japanesehelper

import com.google.gson.Gson
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The backend replies with snake_case usage keys (current_request_tokens,
 * history_tokens, response_tokens, total_tokens); this locks in the
 * @SerializedName mapping against real Gson parsing, the same way
 * ModelComparisonMapperTest does for model_comparison_service.
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
              },
              "compression": {
                "enabled": false,
                "summary_tokens": 0,
                "recent_messages": 4
              }
            }
        """.trimIndent()

        val dto = gson.fromJson(json, AgentChatResponseDto::class.java)
        val domain = dto.toDomain()

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
              },
              "compression": {
                "enabled": false,
                "summary_tokens": null,
                "recent_messages": 2
              }
            }
        """.trimIndent()

        val dto = gson.fromJson(json, AgentChatResponseDto::class.java)
        val domain = dto.toDomain()

        assertNull(domain.usage.currentRequestTokens)
        assertEquals(10, domain.usage.historyTokens)
        assertEquals(6, domain.usage.responseTokens)
        assertNull(domain.usage.totalTokens)
    }

    @Test
    fun `the compression status the backend reports deserializes and maps`() {
        val json = """
            {
              "response": "answer",
              "usage": {
                "current_request_tokens": 17,
                "history_tokens": 812,
                "response_tokens": 240,
                "total_tokens": 1069
              },
              "compression": {
                "enabled": true,
                "summary_tokens": 1245,
                "recent_messages": 6
              }
            }
        """.trimIndent()

        val domain = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain()

        assertTrue(domain.compression.enabled)
        assertEquals(1245, domain.compression.summaryTokens)
        assertEquals(6, domain.compression.recentMessages)
    }

    @Test
    fun `a summary size the backend could not report maps to null, not zero`() {
        val json = """
            {
              "response": "answer",
              "usage": {
                "current_request_tokens": 1,
                "history_tokens": 2,
                "response_tokens": 3,
                "total_tokens": 4
              },
              "compression": {
                "enabled": true,
                "summary_tokens": null,
                "recent_messages": 6
              }
            }
        """.trimIndent()

        val domain = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain()

        assertNull(domain.compression.summaryTokens)
    }

    @Test
    fun `the request sends the chosen mode under the key the backend expects`() {
        val json = gson.toJson(AgentChatRequestDto(message = "Explain 学", compressionEnabled = true))

        assertEquals("""{"message":"Explain 学","compression_enabled":true}""", json)
    }
}
