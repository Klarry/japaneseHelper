package com.japanesehelper

import com.google.gson.Gson
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
}
