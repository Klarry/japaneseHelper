package com.japanesehelper

import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentDigest
import com.japanesehelper.domain.model.AgentInvariant
import com.japanesehelper.domain.model.AgentInvariantCategory
import com.japanesehelper.domain.model.AgentLongTermMemory
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMemoryLayer
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentPipeline
import com.japanesehelper.domain.model.AgentPipelineStage
import com.japanesehelper.domain.model.AgentPipelineStep
import com.japanesehelper.domain.model.AgentPipelineWord
import com.japanesehelper.domain.model.AgentProfilePreset
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.model.AgentShortTermMemory
import com.japanesehelper.domain.model.AgentTaskRefusal
import com.japanesehelper.domain.model.AgentTaskStage
import com.japanesehelper.domain.model.AgentTaskState
import com.japanesehelper.domain.model.AgentTaskTransitionRefused
import com.japanesehelper.domain.model.AgentTokenUsage
import com.japanesehelper.domain.model.AgentToolCall
import com.japanesehelper.domain.model.AgentUserProfile
import com.japanesehelper.domain.model.AgentWorkingMemory
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.AiAgentViewModel
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
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

    private fun invariant(
        id: String = "stack-storage",
        category: AgentInvariantCategory = AgentInvariantCategory.TECHNOLOGY_STACK,
        rule: String = "Storage: JSON files"
    ) = AgentInvariant(id, category, rule)

    private fun task(
        stage: String = "execution",
        currentStep: String = "шаг 2 из 4",
        expectedAction: String = "показать предложения",
        allowedNext: List<String> = listOf("validation")
    ) = AgentTaskState(stage, currentStep, expectedAction, allowedNext)

    private fun memory(
        messages: List<AgentMessage> = emptyList(),
        goals: List<String> = emptyList(),
        constraints: List<String> = emptyList(),
        profile: Map<String, String> = emptyMap()
    ) = AgentMemory(
        shortTerm = AgentShortTermMemory(messages),
        working = AgentWorkingMemory(goals = goals, constraints = constraints),
        longTerm = AgentLongTermMemory(profile = profile)
    )

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
        override suspend fun getMemory(): AgentMemory = AgentMemory()
        override suspend fun clearMemoryLayer(layer: AgentMemoryLayer): AgentMemory = AgentMemory()
        override suspend fun getProfile(): AgentUserProfile = AgentUserProfile()
        override suspend fun updateProfile(profile: AgentUserProfile): AgentUserProfile = profile
        override suspend fun getDigest(): AgentDigest = AgentDigest()
        override suspend fun getTaskState(): AgentTaskState = AgentTaskState()
        override suspend fun clearTaskState(): AgentTaskState = AgentTaskState()
        override suspend fun requestTaskTransition(stage: AgentTaskStage): AgentTaskState =
            AgentTaskState(stage = stage.wireName)

        override suspend fun approveTaskPlan(plan: String): AgentTaskState =
            AgentTaskState(stage = "planning", plan = plan)

        override suspend fun recordTaskValidation(passed: Boolean, notes: String): AgentTaskState =
            AgentTaskState(stage = "validation", validationPassed = passed)
        override suspend fun getInvariants(): List<AgentInvariant> = emptyList()
        override suspend fun saveInvariant(
            id: String?,
            category: AgentInvariantCategory,
            rule: String
        ): List<AgentInvariant> = emptyList()

        override suspend fun deleteInvariant(id: String): List<AgentInvariant> = emptyList()
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

    // --- memory layers ------------------------------------------------------

    @Test
    fun `the memory layers are read back only under the layered strategy`() = runTest {
        given(context = context(strategy = "sliding_window"))

        val viewModel = createViewModel()

        assertNull(viewModel.state.value.memory)
        verify(repository, never()).getMemory()
    }

    @Test
    fun `choosing layered memory loads the three layers`() = runTest {
        given(context = context(strategy = "sliding_window"))
        whenever(repository.getMemory()).thenReturn(memory(profile = mapOf("favorite_word" to "学習")))
        val viewModel = createViewModel()

        viewModel.onStrategySelected(AgentContextStrategy.LAYERED_MEMORY)

        verify(repository, times(1)).setStrategy(AgentContextStrategy.LAYERED_MEMORY)
        assertEquals("学習", viewModel.state.value.memory?.longTerm?.profile?.get("favorite_word"))
    }

    @Test
    fun `leaving layered memory stops showing the layers`() = runTest {
        given(context = context(strategy = "layered_memory"))
        whenever(repository.getMemory()).thenReturn(memory(profile = mapOf("favorite_word" to "学習")))
        val viewModel = createViewModel()

        viewModel.onStrategySelected(AgentContextStrategy.SLIDING_WINDOW)

        assertNull(viewModel.state.value.memory)
    }

    @Test
    fun `clearing one layer shows what the backend left in the other two`() = runTest {
        given(context = context(strategy = "layered_memory"))
        whenever(repository.getMemory()).thenReturn(
            memory(constraints = listOf("уровень N4"), profile = mapOf("favorite_word" to "学習"))
        )
        whenever(repository.clearMemoryLayer(AgentMemoryLayer.WORKING)).thenReturn(
            memory(profile = mapOf("favorite_word" to "学習"))
        )
        val viewModel = createViewModel()

        viewModel.clearMemoryLayer(AgentMemoryLayer.WORKING)

        val memory = viewModel.state.value.memory
        assertEquals(emptyList<String>(), memory?.working?.constraints)
        assertEquals("学習", memory?.longTerm?.profile?.get("favorite_word"))
    }

    @Test
    fun `clearing short-term memory empties the conversation on screen`() = runTest {
        given(
            history = listOf(message("Давай создадим пример")),
            context = context(strategy = "layered_memory")
        )
        whenever(repository.getMemory()).thenReturn(memory(messages = listOf(message("Давай создадим пример"))))
        whenever(repository.clearMemoryLayer(AgentMemoryLayer.SHORT_TERM)).thenReturn(
            memory(profile = mapOf("favorite_word" to "学習"))
        )
        val viewModel = createViewModel()

        viewModel.clearMemoryLayer(AgentMemoryLayer.SHORT_TERM)

        val history = viewModel.state.value.history
        assertTrue(history is AgentHistoryUiState.Loaded)
        assertEquals(0, (history as AgentHistoryUiState.Loaded).messages.size)
        assertNull(viewModel.state.value.lastUsage)
        assertEquals("学習", viewModel.state.value.memory?.longTerm?.profile?.get("favorite_word"))
    }

    @Test
    fun `a failed clear surfaces as an error and leaves the layers alone`() = runTest {
        given(context = context(strategy = "layered_memory"))
        whenever(repository.getMemory()).thenReturn(memory(constraints = listOf("уровень N4")))
        whenever(repository.clearMemoryLayer(AgentMemoryLayer.LONG_TERM))
            .thenThrow(RuntimeException("network down"))
        val viewModel = createViewModel()

        viewModel.clearMemoryLayer(AgentMemoryLayer.LONG_TERM)

        assertFalse(viewModel.state.value.isMemoryWorking)
        assertEquals("network down", viewModel.state.value.contextError)
        assertEquals(listOf("уровень N4"), viewModel.state.value.memory?.working?.constraints)
    }

    // --- user profile -------------------------------------------------------

    @Test
    fun `the profile is loaded when the screen opens`() = runTest {
        given()
        whenever(repository.getProfile()).thenReturn(AgentProfilePreset.A.profile)

        val viewModel = createViewModel()

        verify(repository, times(1)).getProfile()
        assertEquals("N4", viewModel.state.value.profile?.japaneseLevel)
    }

    @Test
    fun `choosing a preset sends that whole profile to the backend`() = runTest {
        given()
        whenever(repository.getProfile()).thenReturn(AgentUserProfile())
        whenever(repository.updateProfile(any())).thenReturn(AgentProfilePreset.B.profile)
        val viewModel = createViewModel()

        viewModel.applyPreset(AgentProfilePreset.B)

        verify(repository, times(1)).updateProfile(AgentProfilePreset.B.profile)
        assertEquals(AgentProfilePreset.B.profile, viewModel.state.value.profile)
    }

    /** The backend owns the profile: a value it normalises comes back and
     * wins over what was sent. */
    @Test
    fun `the screen shows the profile the backend saved, not the one it sent`() = runTest {
        given()
        whenever(repository.getProfile()).thenReturn(AgentUserProfile())
        whenever(repository.updateProfile(any())).thenReturn(
            AgentProfilePreset.A.profile.copy(japaneseLevel = "N3")
        )
        val viewModel = createViewModel()

        viewModel.applyPreset(AgentProfilePreset.A)

        assertEquals("N3", viewModel.state.value.profile?.japaneseLevel)
    }

    @Test
    fun `switching profiles does not touch the conversation`() = runTest {
        given(history = listOf(message("Объясни слово 学習")))
        whenever(repository.getProfile()).thenReturn(AgentUserProfile())
        whenever(repository.updateProfile(any())).thenReturn(AgentProfilePreset.B.profile)
        val viewModel = createViewModel()

        viewModel.applyPreset(AgentProfilePreset.B)

        verify(repository, never()).clearHistory()
        val history = viewModel.state.value.history
        assertEquals(1, (history as AgentHistoryUiState.Loaded).messages.size)
    }

    @Test
    fun `the editor starts from the profile currently in force`() = runTest {
        given()
        whenever(repository.getProfile()).thenReturn(AgentProfilePreset.A.profile)
        val viewModel = createViewModel()

        viewModel.openProfileEditor()

        assertEquals(AgentProfilePreset.A.profile, viewModel.state.value.profileEditor?.profile)
    }

    @Test
    fun `edits are only sent when the dialog is confirmed`() = runTest {
        given()
        whenever(repository.getProfile()).thenReturn(AgentProfilePreset.A.profile)
        whenever(repository.updateProfile(any())).thenReturn(AgentProfilePreset.A.profile)
        val viewModel = createViewModel()
        viewModel.openProfileEditor()

        viewModel.onProfileEdited(AgentProfilePreset.A.profile.copy(japaneseLevel = "N1"))
        viewModel.dismissProfileEditor()

        verify(repository, never()).updateProfile(any())
        assertNull(viewModel.state.value.profileEditor)
    }

    @Test
    fun `confirming the dialog saves what was edited`() = runTest {
        given()
        whenever(repository.getProfile()).thenReturn(AgentProfilePreset.A.profile)
        val edited = AgentProfilePreset.A.profile.copy(japaneseLevel = "N1", answerFormat = "detailed")
        whenever(repository.updateProfile(any())).thenReturn(edited)
        val viewModel = createViewModel()
        viewModel.openProfileEditor()
        viewModel.onProfileEdited(edited)

        viewModel.confirmProfileEdit()

        verify(repository, times(1)).updateProfile(edited)
        assertEquals("N1", viewModel.state.value.profile?.japaneseLevel)
        assertNull(viewModel.state.value.profileEditor)
    }

    @Test
    fun `a failed profile save surfaces as an error and keeps the old profile`() = runTest {
        given()
        whenever(repository.getProfile()).thenReturn(AgentProfilePreset.A.profile)
        whenever(repository.updateProfile(any())).thenThrow(RuntimeException("network down"))
        val viewModel = createViewModel()

        viewModel.applyPreset(AgentProfilePreset.B)

        assertFalse(viewModel.state.value.isProfileWorking)
        assertEquals("network down", viewModel.state.value.profileError)
        assertEquals(AgentProfilePreset.A.profile, viewModel.state.value.profile)
    }

    @Test
    fun `the two demo profiles differ in all four settings`() = runTest {
        val a = AgentProfilePreset.A.profile
        val b = AgentProfilePreset.B.profile

        assertEquals(AgentUserProfile("N4", "simple", "short", "Russian"), a)
        assertEquals(AgentUserProfile("N2", "detailed", "detailed", "English"), b)
        assertEquals(AgentProfilePreset.A, AgentProfilePreset.matching(a))
        assertEquals(AgentProfilePreset.B, AgentProfilePreset.matching(b))
        assertNull(AgentProfilePreset.matching(AgentUserProfile()))
    }

    // --- task state ---------------------------------------------------------

    @Test
    fun `the task state is loaded when the screen opens`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task())

        val viewModel = createViewModel()

        verify(repository, times(1)).getTaskState()
        assertEquals("execution", viewModel.state.value.taskState?.stage)
        assertEquals("шаг 2 из 4", viewModel.state.value.taskState?.currentStep)
    }

    @Test
    fun `the stage is re-read after every message, since any of them can move it`() = runTest {
        given()
        whenever(repository.getTaskState())
            .thenReturn(task(stage = "planning", currentStep = "составляем план"))
            .thenReturn(task(stage = "execution", currentStep = "шаг 1 из 4"))
        whenever(repository.chat(any(), any())).thenReturn(reply("ответ"))
        val viewModel = createViewModel()
        viewModel.onMessageChanged("приступаем")

        viewModel.send()

        verify(repository, times(2)).getTaskState()
        assertEquals("execution", viewModel.state.value.taskState?.stage)
    }

    @Test
    fun `ending the task shows what the backend left`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task())
        whenever(repository.clearTaskState()).thenReturn(
            AgentTaskState(stage = "idle", allowedNext = listOf("planning"))
        )
        val viewModel = createViewModel()

        viewModel.clearTaskState()

        verify(repository, times(1)).clearTaskState()
        assertFalse(viewModel.state.value.taskState!!.isActive)
        assertFalse(viewModel.state.value.isTaskWorking)
    }

    @Test
    fun `ending the task does not touch the conversation`() = runTest {
        given(history = listOf(message("Давай составим план")))
        whenever(repository.getTaskState()).thenReturn(task())
        whenever(repository.clearTaskState()).thenReturn(AgentTaskState())
        val viewModel = createViewModel()

        viewModel.clearTaskState()

        verify(repository, never()).clearHistory()
        val history = viewModel.state.value.history
        assertEquals(1, (history as AgentHistoryUiState.Loaded).messages.size)
    }

    @Test
    fun `a failed read of the task state surfaces as an error`() = runTest {
        given()
        whenever(repository.getTaskState()).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()

        assertEquals("network down", viewModel.state.value.contextError)
        assertNull(viewModel.state.value.taskState)
    }

    @Test
    fun `an idle task is not an active one`() = runTest {
        assertFalse(AgentTaskState().isActive)
        assertFalse(AgentTaskState(stage = "idle").isActive)
        assertTrue(AgentTaskState(stage = "planning").isActive)
        assertTrue(task().isActive)
    }

    // --- invariants ---------------------------------------------------------

    @Test
    fun `the rules are loaded when the screen opens`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(listOf(invariant()))

        val viewModel = createViewModel()

        verify(repository, times(1)).getInvariants()
        assertEquals("Storage: JSON files", viewModel.state.value.invariants?.single()?.rule)
    }

    @Test
    fun `a new rule is sent without an id, since the backend names it`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(emptyList())
        whenever(repository.saveInvariant(anyOrNull(), any(), any())).thenReturn(listOf(invariant()))
        val viewModel = createViewModel()

        viewModel.openInvariantEditor()
        viewModel.onInvariantCategorySelected(AgentInvariantCategory.BUSINESS_RULES)
        viewModel.onInvariantRuleChanged("Только материалы JLPT")
        viewModel.saveInvariant()

        verify(repository, times(1))
            .saveInvariant(null, AgentInvariantCategory.BUSINESS_RULES, "Только материалы JLPT")
        assertNull(viewModel.state.value.invariantEditor)
    }

    @Test
    fun `choosing a rule puts it in the editor and saves it under the same id`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(listOf(invariant()))
        whenever(repository.saveInvariant(anyOrNull(), any(), any())).thenReturn(listOf(invariant()))
        val viewModel = createViewModel()
        viewModel.openInvariantEditor()

        viewModel.onInvariantSelected("stack-storage")
        assertEquals("Storage: JSON files", viewModel.state.value.invariantEditor?.rule)
        viewModel.onInvariantRuleChanged("Storage: JSON files only")
        viewModel.saveInvariant()

        verify(repository, times(1))
            .saveInvariant("stack-storage", AgentInvariantCategory.TECHNOLOGY_STACK, "Storage: JSON files only")
    }

    @Test
    fun `an empty rule is not sent anywhere`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(emptyList())
        val viewModel = createViewModel()
        viewModel.openInvariantEditor()

        viewModel.onInvariantRuleChanged("   ")
        viewModel.saveInvariant()

        verify(repository, never()).saveInvariant(anyOrNull(), any(), any())
        assertNotNull(viewModel.state.value.invariantEditor)
    }

    @Test
    fun `deleting a rule shows what the backend kept`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(listOf(invariant(), invariant(id = "decision-no-sqlite")))
        whenever(repository.deleteInvariant(any())).thenReturn(listOf(invariant()))
        val viewModel = createViewModel()

        viewModel.deleteInvariant("decision-no-sqlite")

        verify(repository, times(1)).deleteInvariant("decision-no-sqlite")
        assertEquals(1, viewModel.state.value.invariants?.size)
        assertFalse(viewModel.state.value.isInvariantsWorking)
    }

    @Test
    fun `a failed save surfaces as an error and keeps the editor open`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(emptyList())
        whenever(repository.saveInvariant(anyOrNull(), any(), any()))
            .thenThrow(RuntimeException("network down"))
        val viewModel = createViewModel()
        viewModel.openInvariantEditor()
        viewModel.onInvariantRuleChanged("Новое правило")

        viewModel.saveInvariant()

        assertEquals("network down", viewModel.state.value.invariantsError)
        assertNotNull(viewModel.state.value.invariantEditor)
    }

    /** The device never inspects a message against the rules - it sends it
     * and shows the reply, like any other. */
    @Test
    fun `asking about a conflict is an ordinary message, not a local check`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(listOf(invariant()))
        whenever(repository.chat(any(), any())).thenReturn(
            reply("Так сделать нельзя: правило «Storage: JSON files». Альтернатива — индекс поверх JSON.")
        )
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Давай возьмём SQLite вместо JSON.")

        viewModel.send()

        verify(repository, times(1)).chat("Давай возьмём SQLite вместо JSON.", AgentContextStrategy.SLIDING_WINDOW)
        val history = viewModel.state.value.history as AgentHistoryUiState.Loaded
        assertTrue(history.messages.last().content.contains("правило"))
    }

    /** Reading them once, when the screen opened, meant one failed read left
     * the block empty until the screen was reopened. */
    @Test
    fun `the rules are read again after an action, not only when the screen opens`() = runTest {
        given()
        whenever(repository.getInvariants())
            .thenThrow(RuntimeException("connection refused"))
            .thenReturn(listOf(invariant()))
        whenever(repository.clearHistory()).thenReturn(Unit)
        val viewModel = createViewModel()
        assertNull(viewModel.state.value.invariants)

        viewModel.clearHistory()

        verify(repository, times(2)).getInvariants()
        assertEquals("Storage: JSON files", viewModel.state.value.invariants?.single()?.rule)
    }

    @Test
    fun `clearing the conversation does not clear the rules`() = runTest {
        given()
        whenever(repository.getInvariants()).thenReturn(listOf(invariant()))
        whenever(repository.clearHistory()).thenReturn(Unit)
        val viewModel = createViewModel()

        viewModel.clearHistory()

        verify(repository, never()).deleteInvariant(any())
        assertEquals(1, viewModel.state.value.invariants?.size)
    }

    // --- controlled transitions (Day 15) ------------------------------------

    private fun refusal(
        requestedStage: String = "done",
        currentStage: String = "planning",
        requiredNext: List<String> = listOf("execution"),
        unmetCondition: String = "the task has not been through execution and validation yet"
    ) = AgentTaskRefusal(
        message = "The task is in '$currentStage' and cannot move to '$requestedStage'.",
        currentStage = currentStage,
        requestedStage = requestedStage,
        requiredNext = requiredNext,
        unmetCondition = unmetCondition
    )

    @Test
    fun `a stage the backend accepts becomes the stage on screen`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "planning", currentStep = "составляем план"))
        whenever(repository.requestTaskTransition(AgentTaskStage.EXECUTION))
            .thenReturn(task(stage = "execution", currentStep = "шаг 1 из 4"))
        val viewModel = createViewModel()

        viewModel.requestTaskTransition(AgentTaskStage.EXECUTION)

        verify(repository, times(1)).requestTaskTransition(AgentTaskStage.EXECUTION)
        assertEquals("execution", viewModel.state.value.taskState?.stage)
        assertNull(viewModel.state.value.taskRefusal)
        assertFalse(viewModel.state.value.isTaskWorking)
    }

    /** The device does not know which moves are legal, so it sends the one it
     * was asked for - that is how it finds out. */
    @Test
    fun `a jump the backend refuses is asked for all the same`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "planning", allowedNext = listOf("execution")))
        whenever(repository.requestTaskTransition(AgentTaskStage.DONE))
            .thenThrow(AgentTaskTransitionRefused(refusal()))
        val viewModel = createViewModel()

        viewModel.requestTaskTransition(AgentTaskStage.DONE)

        verify(repository, times(1)).requestTaskTransition(AgentTaskStage.DONE)
    }

    @Test
    fun `a refused move is shown in the backend's own words`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "planning"))
        whenever(repository.requestTaskTransition(AgentTaskStage.DONE))
            .thenThrow(AgentTaskTransitionRefused(refusal()))
        val viewModel = createViewModel()

        viewModel.requestTaskTransition(AgentTaskStage.DONE)

        val shown = viewModel.state.value.taskRefusal!!
        assertEquals("done", shown.requestedStage)
        assertEquals("planning", shown.currentStage)
        assertEquals(listOf("execution"), shown.requiredNext)
        assertEquals("the task has not been through execution and validation yet", shown.unmetCondition)
    }

    @Test
    fun `a refused move leaves the stage exactly where it was`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "planning", currentStep = "составляем план"))
        whenever(repository.requestTaskTransition(AgentTaskStage.DONE))
            .thenThrow(AgentTaskTransitionRefused(refusal()))
        val viewModel = createViewModel()

        viewModel.requestTaskTransition(AgentTaskStage.DONE)

        assertEquals("planning", viewModel.state.value.taskState?.stage)
        assertEquals("составляем план", viewModel.state.value.taskState?.currentStep)
        assertFalse(viewModel.state.value.isTaskWorking)
        assertNull(viewModel.state.value.contextError)
    }

    @Test
    fun `a refusal is not reported as a failure of the app`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "planning"))
        whenever(repository.requestTaskTransition(AgentTaskStage.DONE))
            .thenThrow(AgentTaskTransitionRefused(refusal()))
        val viewModel = createViewModel()

        viewModel.requestTaskTransition(AgentTaskStage.DONE)

        assertNull(viewModel.state.value.contextError)
    }

    /** A restart reads the same task: the stage it was left in, and the reason
     * it did not advance. */
    @Test
    fun `reopening the screen shows the stage and the refusal the backend kept`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(
            task(stage = "execution", currentStep = "шаг 2 из 4").copy(
                blocked = refusal(currentStage = "execution", requiredNext = listOf("validation"))
            )
        )

        val viewModel = createViewModel()

        assertEquals("execution", viewModel.state.value.taskState?.stage)
        assertEquals("шаг 2 из 4", viewModel.state.value.taskState?.currentStep)
        assertEquals("execution", viewModel.state.value.taskRefusal?.currentStage)
    }

    @Test
    fun `the rest of the way is walked one accepted stage at a time`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "execution"))
        whenever(repository.requestTaskTransition(AgentTaskStage.VALIDATION))
            .thenReturn(task(stage = "validation", currentStep = "проверяем", allowedNext = listOf("done")))
        whenever(repository.recordTaskValidation(true, ""))
            .thenReturn(task(stage = "validation", allowedNext = listOf("done")).copy(validationPassed = true))
        whenever(repository.requestTaskTransition(AgentTaskStage.DONE))
            .thenReturn(task(stage = "done", currentStep = "задача завершена", allowedNext = emptyList()))
        val viewModel = createViewModel()

        viewModel.requestTaskTransition(AgentTaskStage.VALIDATION)
        assertEquals("validation", viewModel.state.value.taskState?.stage)

        viewModel.recordTaskValidation(passed = true)
        assertTrue(viewModel.state.value.taskState!!.validationPassed)

        viewModel.requestTaskTransition(AgentTaskStage.DONE)
        assertEquals("done", viewModel.state.value.taskState?.stage)
        assertTrue(viewModel.state.value.taskState!!.allowedNext.isEmpty())
    }

    @Test
    fun `approving a plan sends the text and shows what came back`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "planning"))
        whenever(repository.approveTaskPlan("4 шага: разбор, примеры, проверка, вывод"))
            .thenReturn(task(stage = "planning", allowedNext = listOf("execution")).copy(plan = "4 шага"))
        val viewModel = createViewModel()

        viewModel.startEditingPlan()
        viewModel.onPlanChanged("4 шага: разбор, примеры, проверка, вывод")
        viewModel.approveTaskPlan()

        verify(repository, times(1)).approveTaskPlan("4 шага: разбор, примеры, проверка, вывод")
        assertNull(viewModel.state.value.planEditor)
        assertEquals("4 шага", viewModel.state.value.taskState?.plan)
    }

    @Test
    fun `an empty plan is not sent anywhere`() = runTest {
        given()
        val viewModel = createViewModel()

        viewModel.startEditingPlan()
        viewModel.onPlanChanged("   ")
        viewModel.approveTaskPlan()

        verify(repository, never()).approveTaskPlan(any())
    }

    @Test
    fun `approving a plan in the wrong stage is refused by the backend, not here`() = runTest {
        given()
        whenever(repository.getTaskState()).thenReturn(task(stage = "execution"))
        whenever(repository.approveTaskPlan(any())).thenThrow(
            AgentTaskTransitionRefused(
                AgentTaskRefusal(
                    message = "The task is in 'execution'",
                    currentStage = "execution",
                    unmetCondition = "the task is not in 'planning'"
                )
            )
        )
        val viewModel = createViewModel()

        viewModel.startEditingPlan()
        viewModel.onPlanChanged("поздний план")
        viewModel.approveTaskPlan()

        verify(repository, times(1)).approveTaskPlan("поздний план")
        assertEquals("the task is not in 'planning'", viewModel.state.value.taskRefusal?.unmetCondition)
        assertEquals("execution", viewModel.state.value.taskState?.stage)
    }

    // --- MCP tool calls (Day 17) -------------------------------------------

    @Test
    fun `the tools behind an answer are attached to that answer`() = runTest {
        given()
        val lookup = AgentToolCall(tool = "get_japanese_word_info", arguments = mapOf("word" to "学習"))
        whenever(repository.chat(any(), any())).thenReturn(
            AgentReply("学習 (がくしゅう) — учёба, N3.", usage(), toolCalls = listOf(lookup))
        )
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Что означает 学習? Дай чтение и перевод.")

        viewModel.send()

        val messages = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages
        assertEquals(AgentMessageRole.USER, messages[0].role)
        assertTrue(messages[0].toolCalls.isEmpty())
        assertEquals("学習 (がくしゅう) — учёба, N3.", messages[1].content)
        assertEquals(listOf(lookup), messages[1].toolCalls)
    }

    @Test
    fun `an answer that needed no lookup carries no tool status`() = runTest {
        given()
        whenever(repository.chat(any(), any())).thenReturn(reply("〜ながら — одновременность."))
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Объясни 〜ながら")

        viewModel.send()

        val messages = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages
        assertTrue(messages.last().toolCalls.isEmpty())
    }

    @Test
    fun `a failed lookup is still shown, as failed`() = runTest {
        given()
        val failed = AgentToolCall(
            tool = "get_japanese_word_info",
            arguments = mapOf("word" to "学習"),
            ok = false,
            error = "the JLPT vocabulary API answered with status 503"
        )
        whenever(repository.chat(any(), any())).thenReturn(
            AgentReply("Не удалось проверить слово в словаре.", usage(), toolCalls = listOf(failed))
        )
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Что означает 学習?")

        viewModel.send()

        val answer = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.last()
        assertFalse(answer.toolCalls.single().ok)
        assertNull(viewModel.state.value.sendError)
    }

    // --- the MCP pipeline on the screen (Day 19) ---------------------------

    @Test
    fun `the chain the backend ran arrives with the answer and is kept on the message`() {
        given()
        val pipeline = AgentPipeline(
            query = "学習",
            steps = listOf(
                AgentPipelineStep(AgentPipelineStage.SEARCH),
                AgentPipelineStep(AgentPipelineStage.SUMMARIZE),
                AgentPipelineStep(AgentPipelineStage.SAVE)
            ),
            found = listOf(AgentPipelineWord("学習", "がくしゅう", "gakushū", "study, learning", "N3")),
            summary = "'学習': 1 JLPT entry, N3 x1.",
            fileName = "20260924T170535-学習.json",
            filePath = "data/pipeline/20260924T170535-学習.json"
        )
        whenever(repository.chat(any(), any())).thenReturn(
            AgentReply("Нашёл, сделал сводку и сохранил.", usage(), toolCalls = emptyList(), pipeline = pipeline)
        )
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Найди информацию о 学習, сделай краткую сводку и сохрани её.")

        viewModel.send()

        val answer = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.last()
        assertEquals(pipeline, answer.pipeline)
        assertTrue(answer.pipeline!!.completed)
        assertEquals("20260924T170535-学習.json", answer.pipeline!!.fileName)
    }

    @Test
    fun `an answer without a chain carries none`() {
        given()
        whenever(repository.chat(any(), any())).thenReturn(reply("Просто ответ"))
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Объясни грамматику")

        viewModel.send()

        val answer = (viewModel.state.value.history as AgentHistoryUiState.Loaded).messages.last()
        assertNull(answer.pipeline)
    }

    // --- the periodic task readout (Day 18) --------------------------------

    private fun digest(runs: Int = 3, collected: Int = 9) = AgentDigest(
        found = true,
        active = true,
        query = "N5 words",
        intervalSeconds = 10,
        runs = runs,
        lastRun = "2026-09-23T17:04:08+00:00",
        itemsCollected = collected,
        summary = "$collected word(s) collected in $runs run(s)"
    )

    @Test
    fun `the periodic task is read when the screen opens`() = runTest {
        given()
        whenever(repository.getDigest()).thenReturn(digest())

        val viewModel = createViewModel()

        verify(repository, times(1)).getDigest()
        assertEquals(3, viewModel.state.value.digest?.runs)
        assertEquals(10, viewModel.state.value.digest?.intervalSeconds)
        assertTrue(viewModel.state.value.digest!!.active)
    }

    /** The task runs on the backend with nobody asking; re-reading it after a
     * message is how the screen catches up. */
    @Test
    fun `the runs that happened between messages show up after the next one`() = runTest {
        given()
        whenever(repository.getDigest())
            .thenReturn(digest(runs = 1, collected = 3))
            .thenReturn(digest(runs = 4, collected = 12))
        whenever(repository.chat(any(), any())).thenReturn(reply("сводка"))
        val viewModel = createViewModel()
        viewModel.onMessageChanged("Покажи последнюю сводку")

        viewModel.send()

        verify(repository, times(2)).getDigest()
        assertEquals(4, viewModel.state.value.digest?.runs)
        assertEquals(12, viewModel.state.value.digest?.itemsCollected)
    }

    /** The block used to move only when the screen was reopened. While the
     * screen is on show it re-reads itself, and stops the moment it is
     * gone - the schedule stays on the backend either way. */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `the periodic task block re-reads itself while the screen is open`() = runTest {
        var reads = 0
        val counting = object : FakeAgentRepository() {
            override suspend fun getDigest(): AgentDigest {
                reads += 1
                return digest(runs = reads, collected = reads * 3)
            }
        }
        val viewModel = createViewModel(counting)
        val onOpen = reads

        viewModel.startWatchingPeriodicTask()
        coroutineRule.scheduler.advanceTimeBy(16_000)
        val whileOpen = reads

        assertTrue("expected repeated reads, got $whileOpen after $onOpen", whileOpen >= onOpen + 3)
        assertEquals(whileOpen, viewModel.state.value.digest?.runs)

        viewModel.stopWatchingPeriodicTask()
        coroutineRule.scheduler.advanceTimeBy(16_000)
        assertEquals(whileOpen, reads)
    }

    @Test
    fun `a digest that cannot be read leaves the conversation alone`() = runTest {
        given()
        whenever(repository.getDigest()).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()

        assertNull(viewModel.state.value.digest)
        assertNull(viewModel.state.value.sendError)
        assertTrue(viewModel.state.value.history is AgentHistoryUiState.Loaded)
    }
}
