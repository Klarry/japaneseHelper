package com.japanesehelper

import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentReply
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
        verify(repository, never()).chat(any())
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
        verify(repository, never()).chat(any())
    }

    @Test
    fun `sending a blank message does not call the repository`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        val viewModel = createViewModel()

        viewModel.onMessageChanged("   ")
        viewModel.send()

        verify(repository, never()).chat(any())
    }

    @Test
    fun `the send request contains only the user's trimmed message`() = runTest {
        // History already has a turn in it - the request must still be just
        // the new message. Conversation context is the backend's job.
        whenever(repository.getHistory()).thenReturn(
            listOf(AgentMessage(AgentMessageRole.ASSISTANT, "earlier answer"))
        )
        whenever(repository.chat(any())).thenReturn(AgentReply("answer"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("  Give me another example for it.  ")
        viewModel.send()

        verify(repository, times(1)).chat("Give me another example for it.")
    }

    @Test
    fun `a successful send appends the user message and the reply, and clears the input`() = runTest {
        whenever(repository.getHistory()).thenReturn(emptyList())
        whenever(repository.chat(any())).thenReturn(AgentReply("学 means to study."))

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
        whenever(repository.chat(any())).thenReturn(AgentReply("学校 means school."))

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
        whenever(repository.chat(any())).thenThrow(RuntimeException("network down"))

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
            override suspend fun chat(message: String): AgentReply {
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

        deferred.complete(AgentReply("answer"))
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
            override suspend fun chat(message: String): AgentReply = AgentReply("unused")
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
}
