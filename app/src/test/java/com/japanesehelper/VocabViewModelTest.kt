package com.japanesehelper

import com.japanesehelper.domain.repository.GoogleSearchRepository
import com.japanesehelper.domain.repository.VocabRepository
import com.japanesehelper.presentation.viewmodel.VocabViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.mock

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
}
