package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.model.RandomWord
import com.japanesehelper.domain.repository.GoogleSearchRepository
import com.japanesehelper.domain.repository.VocabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabViewModel @Inject constructor(
    private val vocabRepository: VocabRepository,
    private val searchRepository: GoogleSearchRepository
) : ViewModel() {

    private val _randomWord = MutableStateFlow<RandomWord?>(null)
    val randomWord = _randomWord.asStateFlow()

    private val _pictureLink = MutableStateFlow<String?>(null)
    val pictureLink = _pictureLink.asStateFlow()

    init {
        getRandomWord()
    }

    fun getRandomWord(
        apiKey: String = "",
        cx: String = "",
    ) {
        viewModelScope.launch {
            try {
                _randomWord.value = vocabRepository.getRandomWord()

                getSearchResult(
                    apiKey = apiKey,
                    cx = cx,
                    meaning = randomWord.value?.meaning.orEmpty()
                )
            } catch (e: Exception) {
                _randomWord.value = RandomWord(word = "Error: ${ e.message }")
                e.printStackTrace()
            }
        }
    }

    fun getSearchResult(
        apiKey: String,
        cx: String,
        meaning: String
    ) {
        viewModelScope.launch {
            try {
                _pictureLink.value = searchRepository.getSearchResults(
                    apiKey = apiKey,
                    cx = cx,
                    query = meaning
                )?.result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}