package com.japanesehelper

import androidx.lifecycle.SavedStateHandle
import com.japanesehelper.domain.model.SUPPORTED_TEMPERATURES
import com.japanesehelper.domain.model.TemperatureDescription
import com.japanesehelper.domain.repository.TemperatureDescriptionRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.TemperatureDescriptionViewModel
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureResultUiState
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

class TemperatureDescriptionViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val repository: TemperatureDescriptionRepository = mock()

    private fun result(temperature: Double) = TemperatureDescription(
        sentence = "sentence for $temperature",
        translation = "translation for $temperature",
        temperature = temperature
    )

    private fun createViewModel(kanji: String = "学"): TemperatureDescriptionViewModel {
        return TemperatureDescriptionViewModel(
            temperatureDescriptionRepository = repository,
            savedStateHandle = SavedStateHandle(
                mapOf(
                    Screens.TemperatureDescription.ARG_KANJI to kanji,
                    Screens.TemperatureDescription.ARG_FURIGANA to "がく",
                    Screens.TemperatureDescription.ARG_MEANING to "study; learning"
                )
            )
        )
    }

    @Test
    fun `nothing is requested when the screen opens`() {
        val viewModel = createViewModel()

        SUPPORTED_TEMPERATURES.forEach { temperature ->
            assertTrue(viewModel.state.value.results[temperature] is TemperatureResultUiState.Idle)
        }
        verify(repository, times(0)).getTemperatureDescription(any(), any())
    }

    @Test
    fun `running one temperature does not affect the others`() = runTest {
        whenever(repository.getTemperatureDescription(any(), any())).thenAnswer { invocation ->
            result(invocation.arguments[1] as Double)
        }

        val viewModel = createViewModel()
        viewModel.run(0.7)

        assertTrue(viewModel.state.value.results[0.7] is TemperatureResultUiState.Success)
        assertTrue(viewModel.state.value.results[0.0] is TemperatureResultUiState.Idle)
        assertTrue(viewModel.state.value.results[1.2] is TemperatureResultUiState.Idle)
        verify(repository, times(1)).getTemperatureDescription("学", 0.7)
    }

    @Test
    fun `an error on one temperature does not affect the others`() = runTest {
        whenever(repository.getTemperatureDescription(any(), eq(0.0)))
            .thenThrow(RuntimeException("boom"))
        whenever(repository.getTemperatureDescription(any(), eq(0.7)))
            .thenReturn(result(0.7))

        val viewModel = createViewModel()
        viewModel.run(0.0)
        viewModel.run(0.7)

        val errorState = viewModel.state.value.results[0.0]
        assertTrue(errorState is TemperatureResultUiState.Error)
        assertEquals("boom", (errorState as TemperatureResultUiState.Error).message)
        assertTrue(viewModel.state.value.results[0.7] is TemperatureResultUiState.Success)
    }

    @Test
    fun `retry re-runs only the failed temperature`() = runTest {
        whenever(repository.getTemperatureDescription(any(), eq(0.0)))
            .thenThrow(RuntimeException("boom"))

        val viewModel = createViewModel()
        viewModel.run(0.0)
        assertTrue(viewModel.state.value.results[0.0] is TemperatureResultUiState.Error)

        whenever(repository.getTemperatureDescription(any(), eq(0.0)))
            .thenReturn(result(0.0))
        viewModel.run(0.0)

        assertTrue(viewModel.state.value.results[0.0] is TemperatureResultUiState.Success)
        verify(repository, times(2)).getTemperatureDescription("学", 0.0)
    }
}
