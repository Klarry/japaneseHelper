package com.japanesehelper

import com.google.gson.Gson
import com.japanesehelper.data.mapper.parseSupportError
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.dto.SupportChatRequestDto
import com.japanesehelper.data.remote.dto.SupportChatResponseDto
import com.japanesehelper.domain.model.SupportStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** POST /support/chat (Day 33) against real Gson parsing: the snake_case keys
 * the backend sends, and the error bodies it refuses with. */
class SupportMapperTest {

    private val gson = Gson()

    private val answeredJson = """
        {
          "request_id": "a1b2c3d4e5f6",
          "answer": "Сброс пароля не влияет на вход через Google.",
          "ticket_id": "TICKET-1042",
          "status": "answered",
          "sources": [
            {"source": "support", "file": "docs/support/faq.md",
             "section": "Вход через Google", "chunk_id": "faq_md_3"},
            {"source": "support", "file": "", "section": "", "chunk_id": "x"}
          ],
          "citations": [
            {"quote": "не затрагивает вход через Google", "source": "docs/support/faq.md",
             "section": "Вход через Google", "chunk_id": "faq_md_3"},
            {"quote": "", "source": "docs/support/faq.md", "section": "", "chunk_id": "faq_md_4"}
          ],
          "rejected_citations": 1,
          "ticket": {
            "ticket_id": "TICKET-1042",
            "status": "open",
            "category": "authentication",
            "subject": "Не могу войти после сброса пароля",
            "description": "…",
            "platform": "android",
            "app_version": "2.4.1",
            "previous_attempts": ["Сбросила пароль по ссылке из письма", "Переустановила приложение"],
            "user_language": "ru",
            "user_subscription": "premium"
          },
          "ticket_facts_used": ["subject", "previous_attempts"],
          "tools_used": [
            {"name": "get_ticket", "server": "support-crm", "ok": true, "ms": 41, "error": ""},
            {"name": "get_user", "server": "support-crm", "ok": true, "ms": 38, "error": ""}
          ],
          "provider": "ollama",
          "model": "qwen3:8b",
          "retrieval": {"best_similarity": 0.41},
          "timing": {"crm_ms": 79, "retrieval_ms": 12, "generation_ms": 5123, "total_ms": 5230}
        }
    """.trimIndent()

    @Test
    fun `an answer maps with its ticket, evidence, tools and timing`() {
        val answer = gson.fromJson(answeredJson, SupportChatResponseDto::class.java).toDomain()

        assertEquals("TICKET-1042", answer.ticketId)
        assertEquals(SupportStatus.ANSWERED, answer.status)
        assertEquals("ollama", answer.provider)
        assertEquals("qwen3:8b", answer.model)
        assertEquals(5230, answer.totalMs)
        assertEquals(listOf("get_ticket", "get_user"), answer.toolsUsed)
        assertEquals(listOf("subject", "previous_attempts"), answer.ticketFactsUsed)

        val ticket = answer.ticket
        assertNotNull(ticket)
        assertEquals("authentication", ticket!!.category)
        assertEquals("2.4.1", ticket.appVersion)
        assertEquals(2, ticket.previousAttempts.size)
        assertEquals("ru", ticket.userLanguage)
    }

    @Test
    fun `a source without a file and a citation without a quote are dropped`() {
        val answer = gson.fromJson(answeredJson, SupportChatResponseDto::class.java).toDomain()

        assertEquals(1, answer.sources.size)
        assertEquals("docs/support/faq.md", answer.sources.single().file)
        assertEquals(1, answer.citations.size)
        assertEquals("faq_md_3", answer.citations.single().chunkId)
        assertTrue(answer.hasDocumentation)
    }

    @Test
    fun `insufficient documentation is reported, with no evidence`() {
        val json = """
            {"answer": "Уточните, пожалуйста, версию приложения.", "ticket_id": "TICKET-1045",
             "status": "insufficient_documentation", "sources": [], "citations": [],
             "ticket": {"ticket_id": "TICKET-1045", "category": "export"},
             "provider": "gemini", "model": "gemini-3.5-flash"}
        """.trimIndent()

        val answer = gson.fromJson(json, SupportChatResponseDto::class.java).toDomain()

        assertEquals(SupportStatus.INSUFFICIENT_DOCUMENTATION, answer.status)
        assertFalse(answer.hasDocumentation)
        assertEquals(0, answer.totalMs)
    }

    @Test
    fun `a ticket without an id is not shown as a ticket`() {
        val json = """{"answer": "a", "ticket_id": "TICKET-1042", "status": "answered", "ticket": {"category": "x"}}"""

        assertNull(gson.fromJson(json, SupportChatResponseDto::class.java).toDomain().ticket)
    }

    @Test
    fun `the request is sent with ticket_id and provider, and no user id`() {
        val json = gson.toJson(SupportChatRequestDto("Почему не работает авторизация?", "TICKET-1042", "ollama"))

        assertTrue(json.contains("\"ticket_id\":\"TICKET-1042\""))
        assertTrue(json.contains("\"provider\":\"ollama\""))
        assertFalse(json.contains("user_id"))
    }

    @Test
    fun `a 404 body is read as ticket_not_found`() {
        val body = """
            {"error": "ticket_not_found", "detail": "there is no ticket TICKET-9999",
             "ticket_id": "TICKET-9999", "request_id": "abc", "provider": null, "reason": null}
        """.trimIndent()

        val error = parseSupportError(body)

        assertNotNull(error)
        assertEquals("ticket_not_found", error!!.error)
        assertEquals("TICKET-9999", error.ticketId)
        assertEquals("there is no ticket TICKET-9999", error.detail)
    }

    @Test
    fun `a validation error or a body that is not JSON is not a support error`() {
        val validation = """{"detail": [{"loc": ["body", "ticket_id"], "msg": "String should match pattern"}]}"""

        assertNull(parseSupportError(validation))
        assertNull(parseSupportError("Internal Server Error"))
        assertNull(parseSupportError("{}"))
    }
}
