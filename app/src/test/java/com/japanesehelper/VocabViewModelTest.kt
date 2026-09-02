package com.japanesehelper

import com.japanesehelper.domain.repository.DescriptionRepository
import com.japanesehelper.domain.repository.ImageSearchRepository
import com.japanesehelper.domain.repository.VocabRepository
import com.japanesehelper.presentation.viewmodel.VocabViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.mockito.kotlin.mock

class VocabViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val vocabRepository: VocabRepository = mock()
    private val imageSearchRepository: ImageSearchRepository = mock()
    private val descriptionRepository: DescriptionRepository = mock()

    private lateinit var viewModel: VocabViewModel

    @Before
    fun setup() {
        viewModel = VocabViewModel(vocabRepository, imageSearchRepository, descriptionRepository)
    }
}
