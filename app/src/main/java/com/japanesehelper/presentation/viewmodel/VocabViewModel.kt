package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.model.RandomWord
import com.japanesehelper.domain.repository.VocabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabViewModel @Inject constructor(
    private val vocabRepository: VocabRepository
) : ViewModel() {

    private val _randomWord = MutableStateFlow<RandomWord?>(null)
    val randomWord = _randomWord.asStateFlow()

    init {
        getRandomWord()
    }

    fun getRandomWord() {
        viewModelScope.launch {
            try {
                _randomWord.value = vocabRepository.getRandomWord()
            } catch (e: Exception) {
                _randomWord.value = RandomWord(word = "Error: ${e.message}")
            }
        }
    }
}