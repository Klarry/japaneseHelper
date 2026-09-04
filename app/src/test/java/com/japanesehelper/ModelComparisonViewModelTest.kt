package com.japanesehelper

import androidx.lifecycle.SavedStateHandle
import com.japanesehelper.data.remote.MODEL_COMPARISON_PROMPT
import com.japanesehelper.domain.model.COMPARISON_MODELS
import com.japanesehelper.domain.model.ModelComparison
import com.japanesehelper.domain.model.ModelComparisonEntry
import com.japanesehelper.domain.repository.ModelComparisonRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.ModelComparisonViewModel
import com.japanesehelper.presentation.viewmodel.screendata.ModelComparisonResultUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ModelComparisonViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val repository: ModelComparisonRepository = mock()

    private fun comparison(withError: Boolean = false) = ModelComparison(
        prompt = "resolved prompt",
        results = COMPARISON_MODELS.mapIndexed { index, model ->
            if (withError && index == 0) {
                ModelComparisonEntry(
                    model = model.modelId,
                    text = null,
                    responseTimeMs = 120,
                    inputTokens = null,
                    outputTokens = null,
                    error = "boom"
                )
            } else {
                ModelComparisonEntry(
                    model = model.modelId,
                    text = "text for ${model.modelId}",
                    responseTimeMs = 200,
                    inputTokens = 10,
                    outputTokens = 20,
                    error = null
                )
            }
        }
    )

    private fun createViewModel(
        kanji: String = "学",
        repository: ModelComparisonRepository = this.repository
    ): ModelComparisonViewModel {
        return ModelComparisonViewModel(
            modelComparisonRepository = repository,
            savedStateHandle = SavedStateHandle(
                mapOf(
                    Screens.ModelComparison.ARG_KANJI to kanji,
                    Screens.ModelComparison.ARG_FURIGANA to "がく",
                    Screens.ModelComparison.ARG_MEANING to "study; learning"
                )
            )
        )
    }

    @Test
    fun `nothing is requested when the screen opens`() = runTest {
        val viewModel = createViewModel()

        assertTrue(viewModel.state.value.result is ModelComparisonResultUiState.Idle)
        verify(repository, times(0)).compareModels(any(), any(), any())
    }

    @Test
    fun `running sends exactly the three configured models with the same kanji and prompt`() = runTest {
        whenever(repository.compareModels(any(), any(), any())).thenReturn(comparison())

        val viewModel = createViewModel()
        viewModel.run()

        val expectedModels = COMPARISON_MODELS.map { it.modelId }
        assertEquals(3, expectedModels.size)
        verify(repository, times(1)).compareModels("学", MODEL_COMPARISON_PROMPT, expectedModels)
    }

    @Test
    fun `a successful run reports a result for every model`() = runTest {
        whenever(repository.compareModels(any(), any(), any())).thenReturn(comparison())

        val viewModel = createViewModel()
        viewModel.run()

        val result = viewModel.state.value.result
        assertTrue(result is ModelComparisonResultUiState.Success)
        assertEquals(3, (result as ModelComparisonResultUiState.Success).comparison.results.size)
    }

    @Test
    fun `one model failing is reported per-entry and does not affect the others`() = runTest {
        whenever(repository.compareModels(any(), any(), any())).thenReturn(comparison(withError = true))

        val viewModel = createViewModel()
        viewModel.run()

        val result = viewModel.state.value.result as ModelComparisonResultUiState.Success
        val failedEntry = result.comparison.results.first { it.model == COMPARISON_MODELS[0].modelId }
        val okEntry = result.comparison.results.first { it.model == COMPARISON_MODELS[1].modelId }

        assertEquals("boom", failedEntry.error)
        assertNull(failedEntry.text)
        assertNull(okEntry.error)
        assertEquals("text for ${okEntry.model}", okEntry.text)
    }

    @Test
    fun `a request-level failure surfaces as an error state`() = runTest {
        whenever(repository.compareModels(any(), any(), any())).thenThrow(RuntimeException("network down"))

        val viewModel = createViewModel()
        viewModel.run()

        val result = viewModel.state.value.result
        assertTrue(result is ModelComparisonResultUiState.Error)
        assertEquals("network down", (result as ModelComparisonResultUiState.Error).message)
    }

    @Test
    fun `retry after an error re-runs the comparison`() = runTest {
        whenever(repository.compareModels(any(), any(), any())).thenThrow(RuntimeException("boom"))

        val viewModel = createViewModel()
        viewModel.run()
        assertTrue(viewModel.state.value.result is ModelComparisonResultUiState.Error)

        whenever(repository.compareModels(any(), any(), any())).thenReturn(comparison())
        viewModel.run()

        assertTrue(viewModel.state.value.result is ModelComparisonResultUiState.Success)
        verify(repository, times(2)).compareModels(any(), any(), any())
    }

    @Test
    fun `state is Loading while the request is in flight, and a second run is ignored`() = runTest {
        val deferred = CompletableDeferred<ModelComparison>()
        var callCount = 0
        val fakeRepository = object : ModelComparisonRepository {
            override suspend fun compareModels(
                kanji: String,
                prompt: String,
                models: List<String>
            ): ModelComparison {
                callCount++
                return deferred.await()
            }
        }

        val viewModel = createViewModel(repository = fakeRepository)

        viewModel.run()
        assertTrue(viewModel.state.value.result is ModelComparisonResultUiState.Loading)

        viewModel.run()
        assertEquals(1, callCount)

        deferred.complete(comparison())
        assertTrue(viewModel.state.value.result is ModelComparisonResultUiState.Success)
    }
}
