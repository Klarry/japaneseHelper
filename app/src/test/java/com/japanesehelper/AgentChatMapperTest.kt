package com.japanesehelper

import com.google.gson.Gson
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentContextResponseDto
import com.japanesehelper.domain.model.AgentMessageRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    // --- MCP tool calls (Day 17) -------------------------------------------

    @Test
    fun `the tool calls the backend agent made deserialize and map`() {
        val json = """
            {
              "response": "学習 (がくしゅう) — учёба, N3.",
              "usage": {"current_request_tokens": 40, "history_tokens": 0, "response_tokens": 8, "total_tokens": 48},
              "compression": {"enabled": false, "summary_tokens": 0, "messages_sent": 0},
              "strategy": "sliding_window",
              "tool_calls": [
                {
                  "tool": "get_japanese_word_info",
                  "arguments": {"word": "学習"},
                  "ok": true,
                  "result": {"query": "学習", "found": true, "matches": [{"word": "学習", "reading": "がくしゅう"}]},
                  "error": ""
                }
              ]
            }
        """.trimIndent()

        val domain = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain()

        assertEquals(1, domain.toolCalls.size)
        assertEquals("get_japanese_word_info", domain.toolCalls[0].tool)
        assertEquals(mapOf("word" to "学習"), domain.toolCalls[0].arguments)
        assertTrue(domain.toolCalls[0].ok)
    }

    @Test
    fun `a failed tool call keeps its failure and its reason`() {
        val json = """
            {
              "response": "Не удалось проверить слово в словаре.",
              "usage": {"current_request_tokens": 40, "history_tokens": 0, "response_tokens": 8, "total_tokens": 48},
              "tool_calls": [
                {"tool": "get_japanese_word_info", "arguments": {"word": "学習"}, "ok": false,
                 "result": null, "error": "the JLPT vocabulary API answered with status 503"}
              ]
            }
        """.trimIndent()

        val call = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain().toolCalls.single()

        assertFalse(call.ok)
        assertEquals("the JLPT vocabulary API answered with status 503", call.error)
    }

    @Test
    fun `a reply from a backend without tool calls maps to none`() {
        val json = """{"response": "answer", "usage": {"history_tokens": 10}}"""

        assertTrue(gson.fromJson(json, AgentChatResponseDto::class.java).toDomain().toolCalls.isEmpty())
    }

    @Test
    fun `an entry without a tool name is dropped`() {
        val json = """
            {"response": "answer", "usage": {}, "tool_calls": [{"arguments": {"word": "学"}, "ok": true}]}
        """.trimIndent()

        assertTrue(gson.fromJson(json, AgentChatResponseDto::class.java).toDomain().toolCalls.isEmpty())
    }
}
