package com.japanesehelper

import com.google.gson.Gson
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.AgentChatRequestDto
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentContextResponseDto
import com.japanesehelper.data.remote.dto.AgentDigestDto
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentPipelineStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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

    // --- the periodic digest (Day 18) --------------------------------------

    @Test
    fun `the digest the backend reports deserializes and maps`() {
        val json = """
            {
              "found": true,
              "summary": "9 word(s) collected for 'N5 words' in 3 run(s) every 10s…",
              "task_id": "digest-1",
              "query": "N5 words",
              "interval_seconds": 10,
              "active": true,
              "runs": 3,
              "failed_runs": 0,
              "last_run": "2026-09-23T17:04:08+00:00",
              "next_run": "2026-09-23T17:04:18+00:00",
              "items_collected": 9,
              "unique_words": 4,
              "levels": {"N5": 9},
              "latest_items": [{"word": "勉強", "reading": "べんきょう"}],
              "last_error": ""
            }
        """.trimIndent()

        val domain = gson.fromJson(json, AgentDigestDto::class.java).toDomain()

        assertTrue(domain.found)
        assertTrue(domain.active)
        assertEquals("N5 words", domain.query)
        assertEquals(10, domain.intervalSeconds)
        assertEquals(3, domain.runs)
        assertEquals(9, domain.itemsCollected)
        assertEquals("2026-09-23T17:04:08+00:00", domain.lastRun)
    }

    @Test
    fun `a backend with no periodic task maps to none`() {
        val json = """{"found": false, "summary": "No periodic digest has been created yet."}"""

        val domain = gson.fromJson(json, AgentDigestDto::class.java).toDomain()

        assertFalse(domain.found)
        assertEquals(0, domain.runs)
        assertEquals(0, domain.intervalSeconds)
    }

    // --- the MCP pipeline (Day 19) -----------------------------------------
    //
    // One answer, three tool calls. The chain itself runs on the backend;
    // what is checked here is that the report of it survives the wire.

    private val pipelineJson = """
        {
          "response": "Нашёл, сделал сводку и сохранил.",
          "usage": {"current_request_tokens": 500, "history_tokens": 0,
                    "response_tokens": 40, "total_tokens": 540},
          "tool_calls": [
            {
              "tool": "search",
              "server": "japanese-data",
              "arguments": {"query": "学習"},
              "ok": true,
              "result": {
                "query": "学習", "found": true, "count": 1,
                "matches": [{"word": "学習", "reading": "がくしゅう", "romaji": "gakushū",
                             "meaning": "study, learning", "jlpt_level": "N3"}],
                "source": "jlpt-vocab-api.vercel.app"
              },
              "error": ""
            },
            {
              "tool": "summarize",
              "server": "processing",
              "arguments": {"findings": {"query": "学習"}},
              "ok": true,
              "result": {"query": "学習", "summary": "'学習': 1 JLPT entry, N3 x1.", "based_on": 1},
              "error": ""
            },
            {
              "tool": "save_to_file",
              "server": "storage",
              "arguments": {"summary": {}, "findings": {}},
              "ok": true,
              "result": {"status": "saved", "file_name": "20260924T170535-学習.json",
                         "path": "data/pipeline/20260924T170535-学習.json", "bytes_written": 940},
              "error": ""
            }
          ]
        }
    """.trimIndent()

    @Test
    fun `the three stages of the chain arrive in the order the backend ran them`() {
        val pipeline = gson.fromJson(pipelineJson, AgentChatResponseDto::class.java).toDomain().pipeline

        assertNotNull(pipeline)
        assertEquals(
            listOf(AgentPipelineStage.SEARCH, AgentPipelineStage.SUMMARIZE, AgentPipelineStage.SAVE),
            pipeline!!.steps.map { it.stage }
        )
        assertTrue(pipeline.completed)
        assertNull(pipeline.failed)
    }

    @Test
    fun `each stage says which MCP server ran it`() {
        val pipeline = gson.fromJson(pipelineJson, AgentChatResponseDto::class.java).toDomain().pipeline!!

        assertEquals(
            listOf("japanese-data", "processing", "storage"),
            pipeline.steps.map { it.server }
        )
        assertEquals(listOf("search", "summarize", "save_to_file"), pipeline.steps.map { it.tool })
    }

    @Test
    fun `a backend that names no server still reports the stages`() {
        val json = """
            {
              "response": "answer",
              "usage": {"current_request_tokens": 1, "history_tokens": 0,
                        "response_tokens": 1, "total_tokens": 2},
              "tool_calls": [
                {"tool": "search", "arguments": {"query": "学習"}, "ok": true,
                 "result": {"query": "学習", "matches": []}, "error": ""},
                {"tool": "summarize", "arguments": {}, "ok": true,
                 "result": {"summary": "none"}, "error": ""}
              ]
            }
        """.trimIndent()

        val pipeline = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain().pipeline!!

        assertEquals(listOf("", ""), pipeline.steps.map { it.server })
        assertEquals(listOf("search", "summarize"), pipeline.steps.map { it.tool })
    }

    @Test
    fun `what each stage produced is read from its own result`() {
        val pipeline = gson.fromJson(pipelineJson, AgentChatResponseDto::class.java).toDomain().pipeline!!

        assertEquals("学習", pipeline.query)
        assertEquals(1, pipeline.found.size)
        assertEquals("がくしゅう", pipeline.found.single().reading)
        assertEquals("study, learning", pipeline.found.single().meaning)
        assertEquals("N3", pipeline.found.single().level)
        assertEquals("'学習': 1 JLPT entry, N3 x1.", pipeline.summary)
        assertEquals("20260924T170535-学習.json", pipeline.fileName)
        assertEquals("data/pipeline/20260924T170535-学習.json", pipeline.filePath)
    }

    @Test
    fun `the tool call lines are still reported next to the chain`() {
        val reply = gson.fromJson(pipelineJson, AgentChatResponseDto::class.java).toDomain()

        assertEquals(listOf("search", "summarize", "save_to_file"), reply.toolCalls.map { it.tool })
        assertTrue(reply.toolCalls.all { it.ok })
    }

    @Test
    fun `a chain that stopped early reports only the stages that ran`() {
        val json = """
            {
              "response": "Не удалось: словарь недоступен.",
              "usage": {"current_request_tokens": 1, "history_tokens": 0,
                        "response_tokens": 1, "total_tokens": 2},
              "tool_calls": [
                {"tool": "search", "server": "japanese-data", "arguments": {"query": "学習"},
                 "ok": false, "result": null, "error": "the dictionary could not be reached"}
              ]
            }
        """.trimIndent()

        val pipeline = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain().pipeline!!

        assertEquals(listOf(AgentPipelineStage.SEARCH), pipeline.steps.map { it.stage })
        assertEquals("japanese-data", pipeline.steps.single().server)
        assertFalse(pipeline.completed)
        assertEquals("the dictionary could not be reached", pipeline.failed?.error)
        assertEquals("", pipeline.fileName)
        assertTrue(pipeline.found.isEmpty())
    }

    @Test
    fun `an ordinary lookup is not reported as a chain`() {
        val json = """
            {
              "response": "学習 (がくしゅう) — study.",
              "usage": {"current_request_tokens": 1, "history_tokens": 0,
                        "response_tokens": 1, "total_tokens": 2},
              "tool_calls": [
                {"tool": "get_japanese_word_info", "arguments": {"word": "学習"}, "ok": true,
                 "result": {"found": true}, "error": ""}
              ]
            }
        """.trimIndent()

        val reply = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain()

        assertNull(reply.pipeline)
        assertEquals(1, reply.toolCalls.size)
    }

    @Test
    fun `a tool that answered with text instead of an object does not break the answer`() {
        val json = """
            {
              "response": "answer",
              "usage": {"current_request_tokens": 1, "history_tokens": 0,
                        "response_tokens": 1, "total_tokens": 2},
              "tool_calls": [
                {"tool": "search", "arguments": {"query": "学習"}, "ok": true,
                 "result": "plain text, not an object", "error": ""},
                {"tool": "summarize", "arguments": {}, "ok": true, "result": null, "error": ""}
              ]
            }
        """.trimIndent()

        val reply = gson.fromJson(json, AgentChatResponseDto::class.java).toDomain()

        assertEquals("answer", reply.text)
        assertEquals(2, reply.pipeline!!.steps.size)
        assertTrue(reply.pipeline!!.found.isEmpty())
        assertEquals("", reply.pipeline!!.summary)
    }
}
