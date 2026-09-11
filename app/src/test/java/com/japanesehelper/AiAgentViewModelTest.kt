package com.japanesehelper

import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentContextStrategy
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
import org.junit.Assert.assertNull
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
    ) = AgentTokenUsage(currentRequestTokens, historyTokens, responseTokens, totalTokens)

    private fun reply(text: String, usage: AgentTokenUsage = usage()) = AgentReply(text, usage)

    private fun message(content: String, role: AgentMessageRole = AgentMessageRole.USER) =
        AgentMessage(role = role, content = content)

    private fun context(
        strategy: String = "sliding_window",
        branch: String = "main",
        branches: List<String> = listOf("main"),
        checkpoints: List<String> = emptyList(),
        facts: Map<String, String> = emptyMap(),
        messages: List<AgentMessage> = emptyList()
    ) = AgentContext(strategy, branch, branches, checkpoints, facts, messages)

    private suspend fun given(
        history: List<AgentMessage> = emptyList(),
        context: AgentContext = context()
    ) {
        whenever(repository.getHistory()).thenReturn(history)
        whenever(repository.getContext()).thenReturn(context)
    }

    /** Every method already behaves, so a test overrides only what it is about. */
    private open class FakeAgentRepository : AgentRepository {
        override suspend fun chat(message: String, strategy: AgentContextStrategy): AgentReply =
            AgentReply("answer", AgentTokenUsage(null, null, null, null))

        override suspend fun getHistory(): List<AgentMessage> = emptyList()
        override suspend fun clearHistory() = Unit
        override suspend fun setStrategy(strategy: AgentContextStrategy) = Unit
        override suspend fun getContext(): AgentContext =
            AgentContext("sliding_window", "main", listOf("main"), emptyList(), emptyMap(), emptyList())

        override suspend fun createCheckpoint(): String = "cp-1"
        override suspend fun createBranch(name: String, checkpoint: String) = Unit
        override suspend fun switchBranch(name: String) = Unit
    }

    // --- opening the screen -------------------------------------------------

    @Test
    fun `history and context are loaded when the screen opens`() = runTest {
        given(history = listOf(message("Explain the kanji 学")))

        val viewModel = createViewModel()

        verify(repository, times(1)).getHistory()
        verify(repository, times(1)).getContext()
        verify(repository, never()).chat(any(), any())
        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Loaded)
        assertEquals(1, (history as AgentHistoryUiState.Loaded).messages.size)
        assertEquals("main", viewModel.state.value.context?.branch)
    }

    @Test
    fun `the strategy shown is the one the backend is already on`() = runTest {
        given(context = context(strategy = "sticky_facts"))

        val viewModel = createViewModel()

        assertEquals(AgentContextStrategy.STICKY_FACTS, viewModel.state.value.strategy)
        verify(repository, never()).setStrategy(any())
    }

    @Test
    fun `a backend on a strategy this screen does not offer is put on one it does`() = runTest {
        // The older full and summary modes are not offered here any more.
        given(context = context(strategy = "summary"))

        val viewModel = createViewModel()

        verify(repository, times(1)).setStrategy(AgentContextStrategy.SLIDING_WINDOW)
        assertEquals(AgentContextStrategy.SLIDING_WINDOW, viewModel.state.value.strategy)
    }

    @Test
    fun `a history loading failure surfaces as an error state`() = runTest {
        whenever(repository.getContext()).thenReturn(context())
        whenever(repository.getHistory()).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()

        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Error)
        assertEquals("network down", (history as AgentHistoryUiState.Error).message)
    }

    @Test
    fun `a context loading failure is shown without breaking the conversation`() = runTest {
        whenever(repository.getHistory()).thenReturn(listOf(message("Explain 学")))
        whenever(repository.getContext()).thenThrow(RuntimeException("context unavailable"))

        val viewModel = createViewModel()

        assertEquals("context unavailable", viewModel.state.value.contextError)
        assertTrue(viewModel.state.value.history is AgentHistoryUiState.Loaded)
    }

    // --- sending ------------------------------------------------------------

    @Test
    fun `the send request contains only the trimmed message and the chosen strategy`() = runTest {
        // History already has a turn in it - the request must still be just the
        // new message and the strategy. Assembling the context is the backend's
        // job, and nothing is trimmed or summarised here.
        given(history = listOf(message("earlier answer", AgentMessageRole.ASSISTANT)))
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("  Give me another example for it.  ")
        viewModel.send()

        verify(repository, times(1))
            .chat("Give me another example for it.", AgentContextStrategy.SLIDING_WINDOW)
    }

    @Test
    fun `a successful send appends the user message and the reply, and clears the input`() = runTest {
        given()
        whenever(repository.chat(any(), any())).thenReturn(reply("学 means to study."))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain the kanji 学")
        viewModel.send()

        assertEquals("", viewModel.state.value.message)
        val messages = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages
        assertEquals(2, messages.size)
        assertEquals(AgentMessageRole.USER, messages[0].role)
        assertEquals("学 means to study.", messages[1].content)
    }

    @Test
    fun `a successful send shows the real usage numbers the backend reported`() = runTest {
        given()
        whenever(repository.chat(any(), any())).thenReturn(
            reply(
                "answer",
                usage(currentRequestTokens = 42, historyTokens = 318, responseTokens = 76, totalTokens = 436)
            )
        )

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()

        val shown = viewModel.state.value.lastUsage
        assertEquals(42, shown?.currentRequestTokens)
        assertEquals(318, shown?.historyTokens)
        assertEquals(436, shown?.totalTokens)
    }

    @Test
    fun `the context is reloaded after every send, because the strategy may have moved it`() = runTest {
        given()
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()

        verify(repository, times(2)).getContext()
    }

    @Test
    fun `a send failure shows an error and keeps the existing conversation and input`() = runTest {
        given(history = listOf(message("Explain 学")))
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
        val fakeRepository = object : FakeAgentRepository() {
            override suspend fun chat(message: String, strategy: AgentContextStrategy): AgentReply {
                callCount++
                return deferred.await()
            }
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

    // --- choosing a strategy ------------------------------------------------

    @Test
    fun `choosing a strategy tells the backend and reloads the context`() = runTest {
        given()

        val viewModel = createViewModel()
        viewModel.onStrategySelected(AgentContextStrategy.STICKY_FACTS)

        verify(repository, times(1)).setStrategy(AgentContextStrategy.STICKY_FACTS)
        verify(repository, times(2)).getContext()
        assertEquals(AgentContextStrategy.STICKY_FACTS, viewModel.state.value.strategy)
    }

    @Test
    fun `the chosen strategy is what the next message is sent with`() = runTest {
        given()
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))

        val viewModel = createViewModel()
        viewModel.onStrategySelected(AgentContextStrategy.BRANCHING)
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()

        verify(repository, times(1)).chat("Explain 学", AgentContextStrategy.BRANCHING)
    }

    @Test
    fun `choosing the strategy already selected does nothing`() = runTest {
        given()

        val viewModel = createViewModel()
        viewModel.onStrategySelected(AgentContextStrategy.SLIDING_WINDOW)

        verify(repository, never()).setStrategy(any())
    }

    // --- what each strategy shows -------------------------------------------

    @Test
    fun `the window the backend reports is what the screen shows`() = runTest {
        // The conversation is longer than the window: the screen shows both,
        // and never works out the window itself.
        val conversation = (0..9).map { message("сообщение $it") }
        given(
            history = conversation,
            context = context(messages = conversation.takeLast(6))
        )

        val viewModel = createViewModel()

        assertEquals(10, (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.size)
        assertEquals(6, viewModel.state.value.context?.messages?.size)
        assertEquals("сообщение 4", viewModel.state.value.context?.messages?.first()?.content)
    }

    @Test
    fun `the facts the backend reports are what the screen shows`() = runTest {
        given(
            context = context(
                strategy = "sticky_facts",
                facts = mapOf("goal" to "сдать N3", "level" to "N4", "constraints" to "15 минут в день")
            )
        )

        val viewModel = createViewModel()

        assertEquals(
            mapOf("goal" to "сдать N3", "level" to "N4", "constraints" to "15 минут в день"),
            viewModel.state.value.context?.facts
        )
    }

    // --- branching ----------------------------------------------------------

    @Test
    fun `creating a checkpoint goes to the backend and reloads the context`() = runTest {
        given()
        whenever(repository.createCheckpoint()).thenReturn("cp-1")

        val viewModel = createViewModel()
        viewModel.createCheckpoint()

        verify(repository, times(1)).createCheckpoint()
        verify(repository, times(2)).getContext()
        assertFalse(viewModel.state.value.isBranchWorking)
    }

    @Test
    fun `the new-branch dialog opens on the newest checkpoint`() = runTest {
        given(context = context(checkpoints = listOf("cp-1", "cp-2")))

        val viewModel = createViewModel()
        viewModel.openNewBranch()

        assertEquals("cp-2", viewModel.state.value.newBranch?.checkpoint)
        assertEquals("", viewModel.state.value.newBranch?.name)
    }

    @Test
    fun `confirming the dialog forks the branch from the chosen checkpoint`() = runTest {
        given(context = context(checkpoints = listOf("cp-1", "cp-2")))

        val viewModel = createViewModel()
        viewModel.openNewBranch()
        viewModel.onNewBranchCheckpointSelected("cp-1")
        viewModel.onNewBranchNameChanged("  formal  ")
        viewModel.confirmNewBranch()

        verify(repository, times(1)).createBranch("formal", "cp-1")
        assertNull(viewModel.state.value.newBranch)
    }

    @Test
    fun `a blank branch name creates nothing`() = runTest {
        given(context = context(checkpoints = listOf("cp-1")))

        val viewModel = createViewModel()
        viewModel.openNewBranch()
        viewModel.onNewBranchNameChanged("   ")
        viewModel.confirmNewBranch()

        verify(repository, never()).createBranch(any(), any())
    }

    @Test
    fun `switching branches reloads that branch's conversation and context`() = runTest {
        given(context = context(branch = "main", branches = listOf("main", "formal")))

        val viewModel = createViewModel()
        viewModel.switchBranch("formal")

        verify(repository, times(1)).switchBranch("formal")
        // The other branch has its own messages, so the chat is reloaded too.
        verify(repository, times(2)).getHistory()
        verify(repository, times(2)).getContext()
    }

    @Test
    fun `switching to the branch already being talked on does nothing`() = runTest {
        given(context = context(branch = "main", branches = listOf("main", "formal")))

        val viewModel = createViewModel()
        viewModel.switchBranch("main")

        verify(repository, never()).switchBranch(any())
    }

    @Test
    fun `a branch failure is shown without touching the conversation`() = runTest {
        given(history = listOf(message("Explain 学")), context = context(branches = listOf("main", "formal")))
        whenever(repository.switchBranch(any())).thenThrow(RuntimeException("branch gone"))

        val viewModel = createViewModel()
        viewModel.switchBranch("formal")

        assertEquals("branch gone", viewModel.state.value.contextError)
        assertEquals(1, (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.size)
    }

    // --- clearing history ---------------------------------------------------

    @Test
    fun `clearing history empties the conversation and the usage`() = runTest {
        given(history = listOf(message("Explain 学")))
        whenever(repository.chat(any(), any())).thenReturn(reply("answer"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain 学")
        viewModel.send()
        assertTrue(viewModel.state.value.lastUsage != null)

        viewModel.clearHistory()

        verify(repository, times(1)).clearHistory()
        assertTrue((viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.isEmpty())
        assertNull(viewModel.state.value.lastUsage)
    }

    @Test
    fun `a clear-history failure keeps the existing conversation visible and shows an error`() = runTest {
        given(history = listOf(message("Explain 学")))
        whenever(repository.clearHistory()).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()
        viewModel.clearHistory()

        assertEquals("network down", viewModel.state.value.clearHistoryError)
        assertEquals(1, (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.size)
    }
}
