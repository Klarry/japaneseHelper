package com.japanesehelper

import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.AiAgentViewModel
import com.japanesehelper.presentation.viewmodel.screendata.AgentChatUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
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

    @Test
    fun `nothing is requested when the screen opens`() = runTest {
        val viewModel = createViewModel()

        assertTrue(viewModel.state.value.result is AgentChatUiState.Idle)
        assertEquals("", viewModel.state.value.message)
        verify(repository, times(0)).chat(any())
    }

    @Test
    fun `typing updates the message without sending a request`() = runTest {
        val viewModel = createViewModel()

        viewModel.onMessageChanged("Explain the kanji 学")

        assertEquals("Explain the kanji 学", viewModel.state.value.message)
        verify(repository, times(0)).chat(any())
    }

    @Test
    fun `asking with a blank message does not call the repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.onMessageChanged("   ")
        viewModel.ask()

        assertTrue(viewModel.state.value.result is AgentChatUiState.Idle)
        verify(repository, times(0)).chat(any())
    }

    @Test
    fun `asking sends the message unchanged and returns the agent's reply`() = runTest {
        whenever(repository.chat(any())).thenReturn(AgentReply("Кандзи 学 значит «учиться»."))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain the kanji 学 in Russian.")
        viewModel.ask()

        verify(repository, times(1)).chat("Explain the kanji 学 in Russian.")
        val result = viewModel.state.value.result
        assertTrue(result is AgentChatUiState.Success)
        assertEquals("Кандзи 学 значит «учиться».", (result as AgentChatUiState.Success).response)
    }

    @Test
    fun `surrounding whitespace is trimmed before sending`() = runTest {
        whenever(repository.chat(any())).thenReturn(AgentReply("answer"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("  Explain 学  ")
        viewModel.ask()

        verify(repository, times(1)).chat("Explain 学")
    }

    @Test
    fun `a repository failure surfaces as an error state`() = runTest {
        whenever(repository.chat(any())).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain 学")
        viewModel.ask()

        val result = viewModel.state.value.result
        assertTrue(result is AgentChatUiState.Error)
        assertEquals("network down", (result as AgentChatUiState.Error).message)
    }

    @Test
    fun `retry after an error asks again`() = runTest {
        whenever(repository.chat(any())).thenThrow(RuntimeException("boom"))

        val viewModel = createViewModel()
        viewModel.onMessageChanged("Explain 学")
        viewModel.ask()
        assertTrue(viewModel.state.value.result is AgentChatUiState.Error)

        whenever(repository.chat(any())).thenReturn(AgentReply("answer"))
        viewModel.ask()

        assertTrue(viewModel.state.value.result is AgentChatUiState.Success)
        verify(repository, times(2)).chat("Explain 学")
    }

    @Test
    fun `state is Loading while the request is in flight, and a second ask is ignored`() = runTest {
        val deferred = CompletableDeferred<AgentReply>()
        var callCount = 0
        val fakeRepository = object : AgentRepository {
            override suspend fun chat(message: String): AgentReply {
                callCount++
                return deferred.await()
            }
        }

        val viewModel = createViewModel(repository = fakeRepository)
        viewModel.onMessageChanged("Explain 学")

        viewModel.ask()
        assertTrue(viewModel.state.value.result is AgentChatUiState.Loading)

        viewModel.ask()
        assertEquals(1, callCount)

        deferred.complete(AgentReply("answer"))
        assertTrue(viewModel.state.value.result is AgentChatUiState.Success)
    }
}
