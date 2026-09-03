package com.japanesehelper

import androidx.lifecycle.SavedStateHandle
import com.japanesehelper.domain.model.Description
import com.japanesehelper.domain.repository.DescriptionRepository
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.AiExplanationViewModel
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionError
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AiExplanationViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val repository: DescriptionRepository = mock()

    private fun createViewModel(
        word: String = "学生",
        meaning: String = "student"
    ): AiExplanationViewModel {
        return AiExplanationViewModel(
            descriptionRepository = repository,
            savedStateHandle = SavedStateHandle(
                mapOf(
                    Screens.AiExplanation.ARG_WORD to word,
                    Screens.AiExplanation.ARG_MEANING to meaning
                )
            )
        )
    }

    @Test
    fun `opening the screen requests the description for the given word`() = runTest {
        whenever(repository.getDescription("student"))
            .thenReturn(Description(uncontrolled = "uncontrolled text", controlled = "controlled text"))

        val viewModel = createViewModel(word = "学生", meaning = "student")

        assertEquals("学生", viewModel.word)
        val state = viewModel.state.value
        assertTrue(state is DescriptionSuccess)
        assertEquals("uncontrolled text", (state as DescriptionSuccess).uncontrolled)
        assertEquals("controlled text", state.controlled)
        verify(repository, times(1)).getDescription(eq("student"))
    }

    @Test
    fun `a failed request surfaces as an error`() = runTest {
        whenever(repository.getDescription("student")).thenThrow(RuntimeException("boom"))

        val viewModel = createViewModel(meaning = "student")

        val state = viewModel.state.value
        assertTrue(state is DescriptionError)
        assertEquals("boom", (state as DescriptionError).message)
    }

    @Test
    fun `retry re-runs the request`() = runTest {
        whenever(repository.getDescription("student")).thenThrow(RuntimeException("boom"))

        val viewModel = createViewModel(meaning = "student")
        assertTrue(viewModel.state.value is DescriptionError)

        whenever(repository.getDescription("student"))
            .thenReturn(Description(uncontrolled = "ok", controlled = "ok"))

        viewModel.retry()

        assertTrue(viewModel.state.value is DescriptionSuccess)
        verify(repository, times(2)).getDescription(eq("student"))
    }
}
