package com.japanesehelper

import com.japanesehelper.domain.model.AgentCompressionStatus
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.model.AgentTokenUsage
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.AiAgentViewModel
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AiAgentViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val repository: AgentRepository = mock()

    private fun createViewModel(repository: AgentRepository = this.repository) =
        AiAgentViewModel(agentRepository = repository)

    private fun usage(
        currentRequestTokens: Int? = 10,
        historyTokens: Int? = 0,
        responseTokens: Int? = 5,
        totalTokens: Int? = 15
    ) = AgentTokenUsage(
        currentRequestTokens = currentRequestTokens,
        historyTokens = historyTokens,
        responseTokens = responseTokens,
        totalTokens = totalTokens
    )

    private fun compression(
        enabled: Boolean = false,
        summaryTokens: Int? = 0,
        messagesSent: Int = 2
    ) = AgentCompressionStatus(
        enabled = enabled,
        summaryTokens = summaryTokens,
        messagesSent = messagesSent
    )

    private fun reply(
        text: String,
        usage: AgentTokenUsage = usage(),
        compression: AgentCompressionStatus = compression()
    ) = AgentReply(text, usage, compression)

    // --- initial load -------------------------------------------------

    @Test
    fun `history is loaded from the repository when the screen opens`() = runTest {
        whenever(repository.getHistory()).thenReturn(
            listOf(
                AgentMessage(AgentMessageRole.USER, "Explain the kanji 学"),
                AgentMessage(AgentMessageRole.ASSISTANT, "学 means to study.")
            )
        )

        val viewModel = createViewModel()

        verify(repository, times(1)).getHistory()
        verify(repository, never()).chat(any(), any())
        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Loaded)
        val messages = (history as AgentHistoryUiState.Loaded).messages
        assertEquals(2, messages.size)
        assertEquals("Explain the kanji 学", messages[0].content)
        assertEquals(AgentMessageRole.ASSISTANT, messages[1].role)
    }

    @Test
    fun `an empty history is loaded as an empty conversation, not an error`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())

        val viewModel = createViewModel()

        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Loaded)
        assertTrue((history as AgentHistoryUiState.Loaded).messages.isEmpty())
    }

    @Test
    fun `a history loading failure surfaces as an error state`() = runTest {
        whenever(repository.getHistory()).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()

        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Error)
        assertEquals("network down", (history as AgentHistoryUiState.Error).message)
    }

    @Test
    fun `retrying after a history loading failure reloads it`() = runTest {
        whenever(repository.getHistory()).thenThrow(RuntimeException("boom"))
        val viewModel = createViewModel()
        assertTrue(viewModel.state.value.history is AgentHistoryUiState.Error)

        whenever(repository.getHistory()).thenReturn(emptyList())
        viewModel.loadHistory()

        assertTrue(viewModel.state.value.history is AgentHistoryUiState.Loaded)
    }

    // --- sending --------------------------------------------------------

    @Test
    fun `typing updates the message without sending a request`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        val viewModel = createViewModel()

        viewModel.onMessageChanged("Explain the kanji 学")

        assertEquals("Explain the kanji 学", viewModel.state.value.message)
        verify(repository, never()).chat(any(), any())
    }

    @Test
    fun `sending a blank message does not call the repository`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        val viewModel = createViewModel()

        viewModel.onMessageChanged("   ")
        viewModel.send()

        verify(repository, never()).chat(any(), any())
    }

    @Test
    fun `the send request contains only the user's trimmed message`() = runTest {
        // History already has a turn in it - the request must still be just
        // the new message. Conversation context is the backend's job.
        whenever(repository.getHistory()).thenReturn(
            listOf(AgentMessage(AgentMessageRole.ASSISTANT, "earlier answer"))
        )
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("  Give me another example for it.  ")
        viewModel.send()

        verify(repository, times(1)).chat("Give me another example for it.", false)
    }

    @Test
    fun `a successful send appends the user message and the reply, and clears the input`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(reply("学 means to study."))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain the kanji 学")
        viewModel.send()

        assertEquals("", viewModel.state.value.message)
        val messages = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages
        assertEquals(2, messages.size)
        assertEquals(AgentMessageRole.USER, messages[0].role)
        assertEquals("Explain the kanji 学", messages[0].content)
        assertEquals(AgentMessageRole.ASSISTANT, messages[1].role)
        assertEquals("学 means to study.", messages[1].content)
    }

    @Test
    fun `sending again appends to the existing conversation instead of replacing it`() = runTest {
        whenever(repository.getHistory()).thenReturn(
            listOf(
                AgentMessage(AgentMessageRole.USER, "Explain the kanji 学"),
                AgentMessage(AgentMessageRole.ASSISTANT, "学 means to study.")
            )
        )
        whenever(repository.chat(any(), any())).thenReturn(reply("学校 means school."))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Give me another example for it.")
        viewModel.send()

        val messages = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages
        assertEquals(4, messages.size)
        assertEquals("Give me another example for it.", messages[2].content)
        assertEquals("学校 means school.", messages[3].content)
    }

    @Test
    fun `a send failure shows an error and keeps the existing conversation and input`() = runTest {
        whenever(repository.getHistory()).thenReturn(
            listOf(AgentMessage(AgentMessageRole.USER, "Explain 学"))
        )
        whenever(repository.chat(any(), any())).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Give me another example for it.")
        viewModel.send()

        assertEquals("network down", viewModel.state.value.sendError)
        assertEquals("Give me another example for it.", viewModel.state.value.message)
        assertEquals(1, (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.size)
    }

    @Test
    fun `sending is Loading while the request is in flight, and a second send is ignored`() = runTest {
        val deferred = CompletableDeferred<AgentReply>()
        var callCount = 0
        val fakeRepository = object : AgentRepository {
            override suspend fun chat(message: String, compressionEnabled: Boolean): AgentReply {
                callCount++
                return deferred.await()
            }
            override suspend fun getHistory(): List<AgentMessage> = emptyList()
            override suspend fun clearHistory() = Unit
        }

        val viewModel = createViewModel(repository = fakeRepository)
        viewModel.onMessageChanged("Explain 学")

        viewModel.send()
        assertTrue(viewModel.state.value.isSending)

        viewModel.send()
        assertEquals(1, callCount)

        deferred.complete(reply("answer"))
        assertFalse(viewModel.state.value.isSending)
    }

    // --- clearing history -------------------------------------------------

    @Test
    fun `clearing history empties the conversation on success`() = runTest {
        whenever(repository.getHistory()).thenReturn(
            listOf(AgentMessage(AgentMessageRole.USER, "Explain 学"))
        )
        whenever(repository.clearHistory()).thenReturn(Unit)

        val viewModel = createViewModel()
        viewModel.clearHistory()

        verify(repository, times(1)).clearHistory()
        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Loaded)
        assertTrue((history as AgentHistoryUiState.Loaded).messages.isEmpty())
    }

    @Test
    fun `a clear-history failure keeps the existing conversation visible and shows an error`() = runTest {
        whenever(repository.getHistory()).thenReturn(
            listOf(AgentMessage(AgentMessageRole.USER, "Explain 学"))
        )
        whenever(repository.clearHistory()).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()
        viewModel.clearHistory()

        assertEquals("network down", viewModel.state.value.clearHistoryError)
        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Loaded)
        assertEquals(1, (history as AgentHistoryUiState.Loaded).messages.size)
    }

    @Test
    fun `clearing history while already clearing is ignored`() = runTest {
        val deferred = CompletableDeferred<Unit>()
        var callCount = 0
        val fakeRepository = object : AgentRepository {
            override suspend fun chat(message: String, compressionEnabled: Boolean): AgentReply = reply("unused")
            override suspend fun getHistory(): List<AgentMessage> = emptyList()
            override suspend fun clearHistory() {
                callCount++
                deferred.await()
            }
        }

        val viewModel = createViewModel(repository = fakeRepository)

        viewModel.clearHistory()
        assertTrue(viewModel.state.value.isClearingHistory)

        viewModel.clearHistory()
        assertEquals(1, callCount)

        deferred.complete(Unit)
    }

    // --- token usage ------------------------------------------------------

    @Test
    fun `there is no token usage to show before anything is sent`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())

        val viewModel = createViewModel()

        assertEquals(null, viewModel.state.value.lastUsage)
    }

    @Test
    fun `a successful send shows the real usage numbers the backend reported`() = runTest {
        // Scenario 1 (short dialogue): small, real numbers from the backend.
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply("answer", usage(currentRequestTokens = 42, historyTokens = 0, responseTokens = 12, totalTokens = 54))
        )

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain the kanji 学")
        viewModel.send()

        val shown = viewModel.state.value.lastUsage
        assertEquals(42, shown?.currentRequestTokens)
        assertEquals(0, shown?.historyTokens)
        assertEquals(12, shown?.responseTokens)
        assertEquals(54, shown?.totalTokens)
    }

    @Test
    fun `usage is replaced, not accumulated, after each send`() = runTest {
        // Scenario 2 (long dialogue): history and total tokens keep growing,
        // and the screen must show the latest numbers, not a running sum.
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply("first answer", usage(currentRequestTokens = 20, historyTokens = 0, responseTokens = 8, totalTokens = 28))
        )
        val viewModel = createViewModel()
        viewModel.onMessageChanged("first message")
        viewModel.send()
        assertEquals(28, viewModel.state.value.lastUsage?.totalTokens)

        whenever(repository.chat(any(), any())).thenReturn(
            reply(
                "second answer",
                usage(currentRequestTokens = 25, historyTokens = 60, responseTokens = 10, totalTokens = 95)
            )
        )
        viewModel.onMessageChanged("second message")
        viewModel.send()

        val shown = viewModel.state.value.lastUsage
        assertEquals(60, shown?.historyTokens)
        assertEquals(95, shown?.totalTokens)
        assertTrue(95 > 28) // total grew across turns, as expected for a growing conversation
    }

    @Test
    fun `usage fields the backend could not report come through as null, not a guess`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply("answer", usage(currentRequestTokens = null, historyTokens = 10, responseTokens = 6, totalTokens = null))
        )

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()

        val shown = viewModel.state.value.lastUsage
        assertEquals(null, shown?.currentRequestTokens)
        assertEquals(null, shown?.totalTokens)
        assertEquals(10, shown?.historyTokens)
    }

    @Test
    fun `a context-limit error is shown to the user and does not touch prior usage or history`() = runTest {
        // Scenario 3: a conversation too long for the model's context window
        // must surface as a clear, visible error - not a crash.
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply("first answer", usage(totalTokens = 30))
        )
        val viewModel = createViewModel()
        viewModel.onMessageChanged("first message")
        viewModel.send()

        whenever(repository.chat(any(), any())).thenThrow(
            RuntimeException("The input token count exceeds the maximum number of tokens allowed")
        )
        viewModel.onMessageChanged("one message too many")
        viewModel.send()

        assertEquals(
            "The input token count exceeds the maximum number of tokens allowed",
            viewModel.state.value.sendError
        )
        // The last successful usage and the conversation so far are untouched.
        assertEquals(30, viewModel.state.value.lastUsage?.totalTokens)
        assertEquals(2, (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.size)
    }

    @Test
    fun `clearing history also clears the shown usage stats`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))
        whenever(repository.clearHistory()).thenReturn(Unit)

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()
        assertTrue(viewModel.state.value.lastUsage != null)

        viewModel.clearHistory()

        assertEquals(null, viewModel.state.value.lastUsage)
    }

    // --- compression mode -------------------------------------------------

    @Test
    fun `compression is off until it is switched on`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())

        val viewModel = createViewModel()

        assertFalse(viewModel.state.value.compressionEnabled)
        assertEquals(null, viewModel.state.value.lastCompression)
    }

    @Test
    fun `switching the mode does not send anything by itself`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        val viewModel = createViewModel()

        viewModel.onCompressionEnabledChanged(true)

        assertTrue(viewModel.state.value.compressionEnabled)
        verify(repository, never()).chat(any(), any())
    }

    @Test
    fun `the chosen mode is what the request asks the backend for`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))

        val viewModel = createViewModel()
        viewModel.onCompressionEnabledChanged(true)
        viewModel.onMessageChanged("Explain the kanji 学")
        viewModel.send()

        // Still just the message and the choice - no summary, no prompt, no
        // history is assembled on the device.
        verify(repository, times(1)).chat("Explain the kanji 学", true)
    }

    @Test
    fun `switching back to off is sent as off`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))

        val viewModel = createViewModel()
        viewModel.onCompressionEnabledChanged(true)
        viewModel.onCompressionEnabledChanged(false)
        viewModel.onMessageChanged("Explain the kanji 学")
        viewModel.send()

        verify(repository, times(1)).chat("Explain the kanji 学", false)
    }

    @Test
    fun `the compression status shown is the one the backend reported`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply("answer", compression = compression(enabled = true, summaryTokens = 1245, messagesSent = 6))
        )

        val viewModel = createViewModel()
        viewModel.onCompressionEnabledChanged(true)
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()

        val shown = viewModel.state.value.lastCompression
        assertEquals(true, shown?.enabled)
        assertEquals(1245, shown?.summaryTokens)
        assertEquals(6, shown?.messagesSent)
    }

    @Test
    fun `the same dialogue in both modes shows the difference in token usage`() = runTest {
        // Scenarios 1 and 2 at the screen level: the same message sent with
        // compression off and then on, with the screen showing exactly what
        // the backend reported for each.
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply(
                "answer",
                usage(currentRequestTokens = 17, historyTokens = 3200, responseTokens = 300, totalTokens = 3517),
                compression(enabled = false, summaryTokens = 0, messagesSent = 24)
            )
        )
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Расскажи про 〜につれて")
        viewModel.send()
        val withoutCompression = viewModel.state.value.lastUsage

        whenever(repository.chat(any(), any())).thenReturn(
            reply(
                "answer",
                usage(currentRequestTokens = 17, historyTokens = 900, responseTokens = 300, totalTokens = 1217),
                compression(enabled = true, summaryTokens = 240, messagesSent = 6)
            )
        )
        viewModel.onCompressionEnabledChanged(true)
        viewModel.onMessageChanged("Расскажи про 〜につれて")
        viewModel.send()
        val withCompression = viewModel.state.value.lastUsage

        assertEquals(3200, withoutCompression?.historyTokens)
        assertEquals(900, withCompression?.historyTokens)
        assertTrue(withCompression!!.totalTokens!! < withoutCompression!!.totalTokens!!)
        assertEquals(6, viewModel.state.value.lastCompression?.messagesSent)
    }

    @Test
    fun `clearing history also clears the shown compression status`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply("answer", compression = compression(enabled = true, summaryTokens = 100, messagesSent = 6))
        )
        whenever(repository.clearHistory()).thenReturn(Unit)

        val viewModel = createViewModel()
        viewModel.onCompressionEnabledChanged(true)
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()
        assertTrue(viewModel.state.value.lastCompression != null)

        viewModel.clearHistory()

        assertEquals(null, viewModel.state.value.lastCompression)
    }

    @Test
    fun `a status with no summary is shown as it is, not hidden or rounded up`() = runTest {
        // What a dialogue too short to have been compressed looks like: the
        // mode is on, but nothing has been summarised yet and every message
        // was sent word for word. The screen must show that plainly - it is
        // the explanation for the usage numbers being identical to a run with
        // compression off.
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any(), any())).thenReturn(
            reply("answer", compression = compression(enabled = true, summaryTokens = 0, messagesSent = 10))
        )

        val viewModel = createViewModel()
        viewModel.onCompressionEnabledChanged(true)
        viewModel.onMessageChanged("вопрос")
        viewModel.send()

        val shown = viewModel.state.value.lastCompression
        assertEquals(true, shown?.enabled)
        assertEquals(0, shown?.summaryTokens)
        assertEquals(10, shown?.messagesSent)
    }
}
