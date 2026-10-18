package com.japanesehelper

import com.japanesehelper.domain.model.ModelProvider
import com.japanesehelper.domain.model.SupportAnswer
import com.japanesehelper.domain.model.SupportRequestFailed
import com.japanesehelper.domain.model.SupportTicket
import com.japanesehelper.domain.model.SupportTicketNotFound
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.SupportViewModel
import com.japanesehelper.presentation.viewmodel.screendata.SupportResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/** Day 33: the support screen shows what the backend sent, or why it sent
 * nothing - and never an answer of its own. */
class SupportViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val repository: AgentRepository = mock()

    private fun answer() = SupportAnswer(
        answer = "Войдите через Google: сброс пароля не меняет вход через Google.",
        ticketId = "TICKET-1042",
        ticket = SupportTicket(ticketId = "TICKET-1042", category = "authentication"),
        provider = "ollama",
        model = "qwen3:8b"
    )

    private fun readyViewModel(repo: AgentRepository = repository): SupportViewModel =
        SupportViewModel(repo).apply {
            onTicketIdChanged("TICKET-1042")
            onQuestionChanged("Почему не работает авторизация?")
        }

    @Test
    fun `the screen opens idle, on the local model, with nothing to send`() {
        val viewModel = SupportViewModel(repository)
        val state = viewModel.state.value

        assertEquals(SupportResult.Idle, state.result)
        assertEquals(ModelProvider.LOCAL, state.provider)
        assertFalse(state.canSend)
    }

    @Test
    fun `a malformed ticket id is not sent`() = runTest {
        val viewModel = SupportViewModel(repository)
        viewModel.onTicketIdChanged("1042")
        viewModel.onQuestionChanged("Почему не работает авторизация?")

        viewModel.send()

        assertFalse(viewModel.state.value.isTicketIdValid)
        verify(repository, never()).askSupport(any(), any(), any())
        assertEquals(SupportResult.Idle, viewModel.state.value.result)
    }

    @Test
    fun `a blank question is not sent`() = runTest {
        val viewModel = SupportViewModel(repository)
        viewModel.onTicketIdChanged("TICKET-1042")
        viewModel.onQuestionChanged("   ")

        viewModel.send()

        verify(repository, never()).askSupport(any(), any(), any())
    }

    @Test
    fun `the request carries the normalized ticket id, the trimmed question and the chosen provider`() = runTest {
        whenever(repository.askSupport(any(), any(), any())).thenReturn(answer())
        val viewModel = SupportViewModel(repository)
        viewModel.onTicketIdChanged("  ticket-1042 ")
        viewModel.onQuestionChanged("  Почему не работает авторизация?  ")
        viewModel.onProviderChanged(ModelProvider.CLOUD)

        viewModel.send()

        verify(repository).askSupport("Почему не работает авторизация?", "TICKET-1042", ModelProvider.CLOUD)
        assertEquals("TICKET-1042", viewModel.state.value.ticketId)
    }

    @Test
    fun `the backend's answer is shown as it came`() = runTest {
        val expected = answer()
        whenever(repository.askSupport(any(), any(), any())).thenReturn(expected)
        val viewModel = readyViewModel()

        viewModel.send()

        val result = viewModel.state.value.result
        assertTrue(result is SupportResult.Answered)
        assertSame(expected, (result as SupportResult.Answered).answer)
    }

    @Test
    fun `a missing ticket is reported as such, with no answer`() = runTest {
        whenever(repository.askSupport(any(), any(), any()))
            .thenThrow(SupportTicketNotFound("TICKET-9999", "there is no ticket TICKET-9999"))
        val viewModel = readyViewModel()
        viewModel.onTicketIdChanged("TICKET-9999")

        viewModel.send()

        assertEquals(SupportResult.TicketNotFound("TICKET-9999"), viewModel.state.value.result)
    }

    @Test
    fun `a named refusal keeps the backend's words and code`() = runTest {
        whenever(repository.askSupport(any(), any(), any()))
            .thenThrow(SupportRequestFailed("provider_unavailable", "Ollama is not running"))
        val viewModel = readyViewModel()

        viewModel.send()

        assertEquals(
            SupportResult.Failed("Ollama is not running", "provider_unavailable"),
            viewModel.state.value.result
        )
    }

    @Test
    fun `a network failure is an error, not an answer`() = runTest {
        whenever(repository.askSupport(any(), any(), any())).thenThrow(RuntimeException("Failed to connect"))
        val viewModel = readyViewModel()

        viewModel.send()

        assertEquals(SupportResult.Failed("Failed to connect"), viewModel.state.value.result)
    }

    @Test
    fun `while a request is in flight the screen is loading and a second send is ignored`() = runTest {
        val pending = CompletableDeferred<SupportAnswer>()
        var calls = 0
        val slow = object : AgentRepository by repository {
            override suspend fun askSupport(message: String, ticketId: String, provider: ModelProvider): SupportAnswer {
                calls++
                return pending.await()
            }
        }
        val viewModel = readyViewModel(slow)

        viewModel.send()
        viewModel.send()

        assertEquals(SupportResult.Loading, viewModel.state.value.result)
        assertFalse(viewModel.state.value.canSend)
        assertEquals(1, calls)

        pending.complete(answer())

        assertTrue(viewModel.state.value.result is SupportResult.Answered)
    }

    @Test
    fun `a failed request can be retried`() = runTest {
        whenever(repository.askSupport(any(), any(), any()))
            .thenThrow(RuntimeException("timeout"))
            .thenReturn(answer())
        val viewModel = readyViewModel()

        viewModel.send()
        assertTrue(viewModel.state.value.result is SupportResult.Failed)

        viewModel.send()
        assertTrue(viewModel.state.value.result is SupportResult.Answered)
    }
}
