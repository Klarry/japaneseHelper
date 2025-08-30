package com.japanesehelper

import app.cash.turbine.test
import com.japanesehelper.domain.model.RandomWord
import com.japanesehelper.domain.model.SearchResult
import com.japanesehelper.domain.repository.GoogleSearchRepository
import com.japanesehelper.domain.repository.VocabRepository
import com.japanesehelper.presentation.viewmodel.VocabViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class VocabViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val vocabRepository: VocabRepository = mock()
    private val searchRepository: GoogleSearchRepository = mock()

    private lateinit var viewModel: VocabViewModel

    @Before
    fun setup() {
        viewModel = VocabViewModel(vocabRepository, searchRepository)
    }

    @Test
    fun `getRandomWord updates state with result`() = runTest {
        val word = RandomWord(word = "hello", meaning = "greeting")
        whenever(vocabRepository.getRandomWord()).thenReturn(word)

        viewModel.getRandomWord()

        viewModel.randomWord.test {
            assertEquals(word, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getRandomWord sets error when exception thrown`() = runTest {
        whenever(vocabRepository.getRandomWord())
            .thenThrow(RuntimeException("Network error"))

        viewModel.getRandomWord()

        viewModel.randomWord.test {
            val result = awaitItem()
            assertTrue(result?.word?.startsWith("Error:") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getSearchResult update state with result`() = runTest {
        val mockResult = SearchResult("https://google.com")

        whenever(
            searchRepository.getSearchResults(apiKey = "", cx = "", query = "")
        ).thenReturn(mockResult)

//        viewModel.getSearchResult(apiKey = "", cx = "", meaning = "")

        viewModel.pictureData.test {
            assertEquals(mockResult.result, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
