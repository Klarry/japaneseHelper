package com.japanesehelper

import com.japanesehelper.data.remote.api.AgentApi
import com.japanesehelper.data.remote.dto.SupportChatRequestDto
import com.japanesehelper.data.remote.dto.SupportChatResponseDto
import com.japanesehelper.data.repository.AgentRepositoryImpl
import com.japanesehelper.domain.model.ModelProvider
import com.japanesehelper.domain.model.SupportRequestFailed
import com.japanesehelper.domain.model.SupportTicketNotFound
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import retrofit2.HttpException
import retrofit2.Response

/** Day 33: the repository turns the backend's refusals into something the
 * screen can tell apart, and invents nothing when a request fails. */
class SupportRepositoryTest {

    private var sent: SupportChatRequestDto? = null

    private fun apiThat(reply: () -> SupportChatResponseDto): AgentApi =
        object : AgentApi by mock<AgentApi>() {
            override suspend fun askSupport(request: SupportChatRequestDto): SupportChatResponseDto {
                sent = request
                return reply()
            }
        }

    private fun httpError(code: Int, body: String) = HttpException(
        Response.error<SupportChatResponseDto>(code, body.toResponseBody("application/json".toMediaType()))
    )

    private fun response() = SupportChatResponseDto(
        requestId = "abc", answer = "ok", ticketId = "TICKET-1042", status = "answered",
        sources = emptyList(), citations = emptyList(), ticket = null, ticketFactsUsed = emptyList(),
        toolsUsed = emptyList(), provider = "ollama", model = "qwen3:8b", timing = null
    )

    private suspend fun failureOf(repository: AgentRepositoryImpl, ticketId: String = "TICKET-1042"): Exception {
        try {
            repository.askSupport("Почему не работает авторизация?", ticketId, ModelProvider.LOCAL)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            return e
        }

        throw AssertionError("expected the request to fail")
    }

    @Test
    fun `the request goes through the existing API with the ticket and the provider's wire name`() = runTest {
        val repository = AgentRepositoryImpl(apiThat { response() })

        val question = "Почему не работает авторизация?"

        val answer = repository.askSupport(question, "TICKET-1042", ModelProvider.CLOUD)

        assertEquals(SupportChatRequestDto(question, "TICKET-1042", "gemini"), sent)
        assertEquals("TICKET-1042", answer.ticketId)
    }

    @Test
    fun `404 ticket_not_found becomes SupportTicketNotFound`() = runTest {
        val body = """
            {"error": "ticket_not_found", "detail": "there is no ticket TICKET-9999", "ticket_id": "TICKET-9999"}
        """.trimIndent()
        val repository = AgentRepositoryImpl(apiThat { throw httpError(404, body) })

        val failure = failureOf(repository, "TICKET-9999")

        assertEquals(SupportTicketNotFound::class, failure::class)
        assertEquals("TICKET-9999", (failure as SupportTicketNotFound).ticketId)
    }

    @Test
    fun `503 provider_unavailable keeps the backend's code and words`() = runTest {
        val body = """
            {"error": "provider_unavailable", "detail": "Ollama is not running", "ticket_id": "TICKET-1042"}
        """.trimIndent()
        val repository = AgentRepositoryImpl(apiThat { throw httpError(503, body) })

        val failure = failureOf(repository) as SupportRequestFailed

        assertEquals("provider_unavailable", failure.error)
        assertEquals("Ollama is not running", failure.message)
    }

    @Test
    fun `409 for a ticket whose user cannot be verified is a refusal, not a missing ticket`() = runTest {
        val body = """
            {"error": "ticket_user_not_found", "detail": "the ticket's user is unknown", "ticket_id": "TICKET-1047"}
        """.trimIndent()
        val repository = AgentRepositoryImpl(apiThat { throw httpError(409, body) })

        val failure = failureOf(repository, "TICKET-1047")

        assertEquals("ticket_user_not_found", (failure as SupportRequestFailed).error)
    }

    @Test
    fun `an error body that is not a support error leaves the HTTP exception as it was`() = runTest {
        val repository = AgentRepositoryImpl(
            apiThat { throw httpError(422, """{"detail": [{"msg": "String should match pattern"}]}""") }
        )

        val failure = failureOf(repository)

        assertEquals(HttpException::class, failure::class)
        assertEquals(422, (failure as HttpException).code())
    }
}
