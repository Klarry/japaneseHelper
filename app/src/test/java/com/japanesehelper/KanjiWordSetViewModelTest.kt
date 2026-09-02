package com.japanesehelper

import androidx.lifecycle.SavedStateHandle
import com.japanesehelper.domain.model.ExperimentType
import com.japanesehelper.domain.model.KanjiWordSet
import com.japanesehelper.domain.repository.KanjiWordSetRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.KanjiWordSetViewModel
import com.japanesehelper.presentation.viewmodel.screendata.TabUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class KanjiWordSetViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val repository: KanjiWordSetRepository = mock()

    private fun result(experimentType: ExperimentType) = KanjiWordSet(
        prompt = "prompt for $experimentType",
        words = listOf("学生", "学校"),
        cost = 4,
        value = 16
    )

    private fun createViewModel(kanji: String = "学"): KanjiWordSetViewModel {
        return KanjiWordSetViewModel(
            kanjiWordSetRepository = repository,
            savedStateHandle = SavedStateHandle(mapOf(Screens.KanjiWordSet.ARG_KANJI to kanji))
        )
    }

    @Test
    fun `opening the screen requests all four experiments concurrently`() = runTest {
        whenever(repository.getKanjiWordSet(any(), any())).thenAnswer { invocation ->
            result(invocation.arguments[1] as ExperimentType)
        }

        val viewModel = createViewModel()

        ExperimentType.entries.forEach { experimentType ->
            assertTrue(viewModel.state.value.tabs[experimentType] is TabUiState.Success)
            verify(repository, times(1)).getKanjiWordSet("学", experimentType)
        }
    }

    @Test
    fun `switching tabs never issues an extra request`() = runTest {
        whenever(repository.getKanjiWordSet(any(), any())).thenAnswer { invocation ->
            result(invocation.arguments[1] as ExperimentType)
        }

        val viewModel = createViewModel()
        viewModel.selectTab(ExperimentType.PROMPT)
        viewModel.selectTab(ExperimentType.EXPERTS)
        viewModel.selectTab(ExperimentType.DIRECT)

        assertEquals(ExperimentType.DIRECT, viewModel.state.value.selectedTab)
        ExperimentType.entries.forEach { experimentType ->
            verify(repository, times(1)).getKanjiWordSet(eq("学"), eq(experimentType))
        }
    }

    @Test
    fun `an error on one tab does not affect the others`() = runTest {
        whenever(repository.getKanjiWordSet(any(), eq(ExperimentType.DIRECT)))
            .thenThrow(RuntimeException("boom"))
        ExperimentType.entries.filter { it != ExperimentType.DIRECT }.forEach { experimentType ->
            whenever(repository.getKanjiWordSet(any(), eq(experimentType)))
                .thenReturn(result(experimentType))
        }

        val viewModel = createViewModel()

        val directState = viewModel.state.value.tabs[ExperimentType.DIRECT]
        assertTrue(directState is TabUiState.Error)
        assertEquals("boom", (directState as TabUiState.Error).message)

        ExperimentType.entries.filter { it != ExperimentType.DIRECT }.forEach { experimentType ->
            assertTrue(viewModel.state.value.tabs[experimentType] is TabUiState.Success)
        }
    }

    @Test
    fun `retry re-runs only the failed tab`() = runTest {
        whenever(repository.getKanjiWordSet(any(), eq(ExperimentType.DIRECT)))
            .thenThrow(RuntimeException("boom"))
        ExperimentType.entries.filter { it != ExperimentType.DIRECT }.forEach { experimentType ->
            whenever(repository.getKanjiWordSet(any(), eq(experimentType)))
                .thenReturn(result(experimentType))
        }

        val viewModel = createViewModel()
        assertTrue(viewModel.state.value.tabs[ExperimentType.DIRECT] is TabUiState.Error)

        whenever(repository.getKanjiWordSet(any(), eq(ExperimentType.DIRECT)))
            .thenReturn(result(ExperimentType.DIRECT))

        viewModel.retry(ExperimentType.DIRECT)

        assertTrue(viewModel.state.value.tabs[ExperimentType.DIRECT] is TabUiState.Success)
        verify(repository, times(2)).getKanjiWordSet(eq("学"), eq(ExperimentType.DIRECT))
        ExperimentType.entries.filter { it != ExperimentType.DIRECT }.forEach { experimentType ->
            verify(repository, times(1)).getKanjiWordSet(eq("学"), eq(experimentType))
        }
    }
}
